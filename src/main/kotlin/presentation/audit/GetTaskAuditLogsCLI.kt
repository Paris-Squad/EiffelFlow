package org.example.presentation.audit

import kotlinx.coroutines.runBlocking
import org.example.domain.model.AuditLog
import org.example.domain.usecase.audit.GetTaskAuditUseCase
import org.example.presentation.BaseCli
import org.example.presentation.helper.extensions.toFormattedDateTime
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

class GetTaskAuditLogsCLI(
    private val getTaskAuditLogsUseCase: GetTaskAuditUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {
    fun start() {
        tryStartCli {
            printer.displayLn("Enter Task ID to retrieve audit logs:")
            val taskIdInput = inputReader.readString()

            if (taskIdInput.isNullOrBlank()) {
                printer.displayLn("Task ID cannot be empty. Please provide a valid UUID.")
                return@tryStartCli
            }

            val taskId = UUID.fromString(taskIdInput)

            getAuditLogsForTask(taskId)

        }
    }

    @VisibleForTesting
    internal fun getAuditLogsForTask(taskId: UUID) {
        runBlocking {
            val taskAuditLogs = getTaskAuditLogsUseCase.getTaskAuditLogsById(taskId)
            if (taskAuditLogs.isEmpty()) {
                printer.displayLn("No audit logs found for Task ID: $taskId.")
                return@runBlocking
            }

            printer.displayLn("=== Audit Logs on Task: $taskId ===")

            taskAuditLogs.forEach { auditLog -> displayTaskAuditDetails(auditLog) }

            printer.displayLn("=== End of Audit Logs ===")
        }
    }


    @VisibleForTesting
    internal fun displayTaskAuditDetails(auditLog: AuditLog) {
        val paddedLabel = { label: String -> label.padEnd(15) }

        printer.displayLn("[Task] ${auditLog.actionType.actionName} '${auditLog.itemName}'")
        printer.displayLn("  ${paddedLabel("Audit ID")} : ${auditLog.auditId}")
        printer.displayLn("  ${paddedLabel("Date & Time")} : ${auditLog.auditTime.toFormattedDateTime()}")
        printer.displayLn("  ${paddedLabel("Modified By")} : ${auditLog.editorName}")
        printer.displayLn("  ${paddedLabel("Field Changed")} : ${auditLog.changedField ?: "Not Available"}")
        printer.displayLn("  ${paddedLabel("Old")} : ${auditLog.oldValue ?: "Not Available"}")
        printer.displayLn("  ${paddedLabel("New")} : ${auditLog.newValue ?: "Not Available"}")
        printer.displayLn("-".repeat(50))
    }

}