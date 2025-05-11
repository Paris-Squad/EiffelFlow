package org.example.presentation.audit

import kotlinx.coroutines.runBlocking
import org.example.domain.model.AuditLog
import org.example.domain.usecase.audit.GetAllAuditLogsUseCase
import org.example.presentation.helper.extensions.toFormattedDateTime
import org.example.presentation.io.Printer
import org.jetbrains.annotations.VisibleForTesting

class GetAuditLogsCLI(
    private val getAuditLogsUseCase: GetAllAuditLogsUseCase,
    private val printer: Printer
) {
    fun start() {
        runBlocking {
            val allAuditLogs = getAuditLogsUseCase.getAllAuditLogs()
            if (allAuditLogs.isEmpty()) {
                printer.displayLn("No audit logs found for any project or task.")
                return@runBlocking
            }

            printer.displayLn("=== Audit Logs Overview ===")

            allAuditLogs.forEach { auditLog -> displayAuditLogs(auditLog) }

            printer.displayLn("=== End of Audit Logs ===")
        }
    }

    @VisibleForTesting
    internal fun displayAuditLogs(auditLog: AuditLog) {
        val labelPadded = { label: String -> label.padEnd(15) }

        printer.displayLn("${auditLog.actionType.actionName} '${auditLog.itemName}'")
        printer.displayLn("  ${labelPadded("Audit ID")} : ${auditLog.auditId}")
        printer.displayLn("  ${labelPadded("Date & Time")} : ${auditLog.auditTime.toFormattedDateTime()}")
        printer.displayLn("  ${labelPadded("Modified By")} : ${auditLog.editorName}")
        printer.displayLn("  ${labelPadded("Field Changed")} : ${auditLog.changedField ?: "Not Available"}")
        printer.displayLn("  ${labelPadded("Old Value")} : ${auditLog.oldValue ?: "Not Available"}")
        printer.displayLn("  ${labelPadded("New Value")} : ${auditLog.newValue ?: "Not Available"}")
        printer.displayLn("-".repeat(50))
    }
}