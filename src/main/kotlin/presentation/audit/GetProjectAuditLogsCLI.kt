package org.example.presentation.audit

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.AuditLogAction
import org.example.domain.usecase.audit.GetProjectAuditUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

class GetProjectAuditLogsCLI(
    private val getProjectAuditUseCase: GetProjectAuditUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {
    fun getProjectAuditLogsInput() {
        tryStartCli {
            printer.displayLn("Please enter the Project ID to retrieve the Audit Logs:")
            val input = inputReader.readString()

            if (input.isNullOrBlank()) {
                printer.displayLn("Project ID cannot be left blank. Please provide a valid ID.")
                return@tryStartCli
            }

            val projectId = UUID.fromString(input.trim())
            getProjectAuditLogs(projectId)

        }
    }

    private fun getProjectAuditLogs(projectId: UUID) {
        runBlocking {
            val projectAuditLogs = getProjectAuditUseCase.getProjectAuditLogsById(projectId)
            if (projectAuditLogs.isEmpty()) {
                printer.displayLn("No audit logs found for the specified Project ID: $projectId.")
                return@runBlocking
            }

            val projectLog = projectAuditLogs.firstOrNull { it.itemId == projectId }
            val projectName = if (projectLog?.itemName.isNullOrBlank()) "Unnamed Project" else projectLog.itemName
            printer.displayLn("Audit Logs for Project: '$projectName'")

            projectAuditLogs.forEach { log ->
                val logType = if (log.itemId == projectId) "Project" else "Task"

                val actionType = when (log.actionType) {
                    AuditLogAction.CREATE -> "Created"
                    AuditLogAction.UPDATE -> "Updated"
                    AuditLogAction.DELETE -> "Deleted"
                }

                printer.displayLn("[$logType] $actionType ${log.itemName}")
                printer.displayLn("  Audit ID     : ${log.auditId}")
                printer.displayLn("  Date         : ${log.auditTime.date} / Time: ${formatTime(log.auditTime)}")
                printer.displayLn("  Modified By  : ${log.editorName}")
                printer.displayLn("  Field Changed: ${log.changedField ?: "Not Available"}")
                printer.displayLn("    Old        : ${log.oldValue ?: "Not Available"}")
                printer.displayLn("    New        : ${log.newValue ?: "Not Available"}")
                printer.displayLn("-".repeat(50))
            }
        }
    }

    @VisibleForTesting
    internal fun formatTime(time: LocalDateTime): String {
        val hour = time.hour
        val minute = time.minute
        val amPm = if (hour >= 12) "PM" else "AM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return "$displayHour:${minute.toString().padStart(2, '0')} $amPm"
    }
}