package org.example.presentation.audit

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.example.domain.model.AuditLogAction
import org.example.domain.usecase.audit.GetAllAuditLogsUseCase
import org.example.presentation.io.Printer
import org.jetbrains.annotations.VisibleForTesting

class GetAuditLogsCLI(
    private val getAuditLogsUseCase: GetAllAuditLogsUseCase,
    private val printer: Printer
){
    fun getAllAuditLogs() {
        runBlocking {
            val allAuditLogs = getAuditLogsUseCase.getAllAuditLogs()
            if (allAuditLogs.isEmpty()) {
                printer.displayLn("No audit logs found for any project or task.")
                return@runBlocking
            }

            printer.displayLn("=== Audit Logs Overview ===")

            allAuditLogs.forEach { log ->
                val actionType = when (log.actionType) {
                    AuditLogAction.CREATE -> "Created"
                    AuditLogAction.UPDATE -> "Updated"
                    AuditLogAction.DELETE -> "Deleted"
                }

                printer.displayLn("----- [Audit] ${actionType}: ${log.itemName} -----")
                printer.displayLn("  Audit ID       : ${log.auditId}")
                printer.displayLn("  Date           : ${log.auditTime.date} / Time: ${formatTime(log.auditTime)}")
                printer.displayLn("  Modified By    : ${log.editorName}")
                printer.displayLn("  Field Changed  : ${log.changedField ?: "Not Available"}")
                printer.displayLn("    Old Value      : ${log.oldValue ?: "Not Available"}")
                printer.displayLn("    New Value      : ${log.newValue ?: "Not Available"}")
                printer.displayLn("-".repeat(50))
            }

            printer.displayLn("=== End of Audit Logs ===")
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
