package org.example.presentation.audit

import kotlinx.coroutines.runBlocking
import org.example.domain.model.AuditLog
import org.example.domain.usecase.audit.GetProjectAuditUseCase
import org.example.presentation.BaseCli
import org.example.presentation.helper.extensions.toFormattedDateTime
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import java.util.UUID

class GetProjectAuditLogsCLI(
    private val getProjectAuditUseCase: GetProjectAuditUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {

    fun start() {
        tryStartCli {
            printer.displayLn("Please enter the Project ID to retrieve the Audit Logs:")
            val input = inputReader.readString()

            if (input.isNullOrBlank()) {
                printer.displayLn("Project ID cannot be left blank. Please provide a valid ID.")
                return@tryStartCli
            }

            val projectId = UUID.fromString(input.trim())
            showProjectAuditLogs(projectId)
        }
    }

    private fun showProjectAuditLogs(projectId: UUID) {
        runBlocking {
            val projectAuditLogs = getProjectAuditUseCase.getProjectAuditLogsById(projectId)
            if (projectAuditLogs.isEmpty()) {
                printer.displayLn("No audit logs found for the specified Project ID: $projectId.")
                return@runBlocking
            }

            val projectName = getProjectNameFromLogs(projectAuditLogs, projectId)
            printer.displayLn("Audit Logs for Project: '$projectName'")

            projectAuditLogs.forEach { auditLog ->
                val isProjectLog = auditLog.itemId == projectId
                preparingAuditLogToDisplay(auditLog, isProjectLog)
            }
            printer.displayLn("=== End of Audit Logs ===")
        }
    }

    private fun preparingAuditLogToDisplay(auditLog: AuditLog, isProjectLog: Boolean) {
        val paddedLabel = { label: String -> label.padEnd(15) }
        val logType = if (isProjectLog) "[Project]" else "[Task]"

        printer.displayLn("${logType.padEnd(10)} ${auditLog.actionType.actionName} '${auditLog.itemName}'")
        displayProjectAuditDetails(auditLog, paddedLabel)
    }

    private fun displayProjectAuditDetails(auditLog: AuditLog, paddedLabel: (String) -> String) {
        printer.displayLn("  ${paddedLabel("Audit ID")} : ${auditLog.auditId}")
        printer.displayLn("  ${paddedLabel("Date & Time")} : ${auditLog.auditTime.toFormattedDateTime()}")
        printer.displayLn("  ${paddedLabel("Modified By")} : ${auditLog.editorName}")
        printer.displayLn("  ${paddedLabel("Field Changed")} : ${auditLog.changedField ?: "Not Available"}")
        printer.displayLn("  ${paddedLabel("Old")} : ${auditLog.oldValue ?: "Not Available"}")
        printer.displayLn("  ${paddedLabel("New")} : ${auditLog.newValue ?: "Not Available"}")
        printer.displayLn("-".repeat(50))
    }

    private fun getProjectNameFromLogs(projectAuditLogs: List<AuditLog>, projectId: UUID): String {
        val projectLog = projectAuditLogs.firstOrNull { it.itemId == projectId }
        return projectLog?.itemName?.takeIf { it.isNotBlank() } ?: "Unnamed Project"
    }
}
