package org.example.data.storage.audit

import org.example.data.storage.CsvStorageManager
import org.example.data.storage.mapper.AuditCsvMapper
import org.example.domain.model.AuditLog
import org.example.domain.exception.EiffelFlowException
import java.util.UUID

class AuditDataSourceImpl(
    private val auditMapper: AuditCsvMapper,
    private val csvManager: CsvStorageManager
) : AuditDataSource {
    override fun createAuditLog(auditLog: AuditLog): Result<AuditLog> {
        return try {
            val line = listOf(
                auditLog.auditId.toString(),
                auditLog.itemId.toString(),
                auditLog.itemName,
                auditLog.userId.toString(),
                auditLog.editorName,
                auditLog.actionType.name,
                auditLog.auditTime.toString(),
                auditLog.changedField,
                auditLog.oldValue,
                auditLog.newValue
            ).joinToString(",")

            csvManager.writeLinesToFile(line)
            Result.success(auditLog)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getItemAuditLogById(itemId: UUID): Result<List<AuditLog>> {
            val lines = csvManager.readLinesFromFile()
            if (lines.isEmpty())  return Result.success(emptyList())

            val auditLogs = lines.map { line ->
                        auditMapper.mapFrom(line)
                }.filter { it.itemId == itemId }

        return if (auditLogs.isEmpty()) {
            Result.failure(EiffelFlowException.ElementNotFoundException("Audit logs not found for item ID: $itemId"))
        } else {
            Result.success(auditLogs)
        }
    }

    override fun getAuditLogs(): Result<List<AuditLog>> {
            val lines = csvManager.readLinesFromFile()
            if (lines.isEmpty()) return Result.failure(EiffelFlowException.ElementNotFoundException("Audit logs not found"))

        val logs = lines.mapNotNull { line ->
            try {
                auditMapper.mapFrom(line)
            } catch (e: Exception) {
                null
            }
        }

        return if (logs.isEmpty()) {
                Result.failure(EiffelFlowException.ElementNotFoundException("Audit logs not found"))
            } else {
                Result.success(logs)
            }

    }

    companion object {
        const val FILE_NAME: String = "audits.csv"
    }

}
