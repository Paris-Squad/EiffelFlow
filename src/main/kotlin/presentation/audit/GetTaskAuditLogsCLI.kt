package org.example.presentation.audit

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.example.domain.model.AuditLogAction
import org.example.domain.usecase.audit.GetTaskAuditUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

class GetTaskAuditLogsCLI(
    private val getTaskAuditLogsUseCase: GetTaskAuditUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {
    fun getTaskAuditLogsInput() {
        tryStartCli {
            printer.displayLn("Enter Task ID to retrieve audit logs:")
            val taskIdInput = inputReader.readString()

            if (taskIdInput.isNullOrBlank()) {
                printer.displayLn("Task ID cannot be empty. Please provide a valid UUID.")
                return@tryStartCli
            }

            val taskId = try {
                UUID.fromString(taskIdInput)
            } catch (e: IllegalArgumentException){
                printer.displayLn("Invalid Task ID format. Please enter a valid UUID.")
                return@tryStartCli
            }

            displayAuditLogsForTask(taskId)

        }
    }

   private fun displayAuditLogsForTask(taskId: UUID) {
        runBlocking {
            val taskAuditLogs = getTaskAuditLogsUseCase.getTaskAuditLogsById(taskId)
            if (taskAuditLogs.isEmpty()) {
                printer.displayLn("No audit logs found for Task ID: $taskId.")
                return@runBlocking
            }

            taskAuditLogs.forEach { auditLog ->
                val actionType = when (auditLog.actionType) {
                    AuditLogAction.CREATE -> "Created"
                    AuditLogAction.UPDATE -> "Updated"
                    AuditLogAction.DELETE -> "Deleted"
                }

                printer.displayLn("[Task] $actionType ${auditLog.itemName}")
                printer.displayLn("  Audit ID     : ${auditLog.auditId}")
                printer.displayLn("  Date         : ${auditLog.auditTime.date} / Time: ${formatTime(auditLog.auditTime)}")
                printer.displayLn("  Modified By  : ${auditLog.editorName}")
                printer.displayLn("  Field Changed: ${auditLog.changedField ?: "Not Available"}")
                printer.displayLn("    Old        : ${auditLog.oldValue ?: "Not Available"}")
                printer.displayLn("    New        : ${auditLog.newValue ?: "Not Available"}")
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