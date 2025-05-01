package org.example.data.storage.audit

import kotlinx.datetime.LocalDateTime
import org.example.data.storage.CsvStorageManager
import org.example.data.storage.Mapper
import org.example.domain.model.entities.AuditAction
import org.example.domain.model.entities.AuditLog
import org.example.domain.model.exception.EiffelFlowException
import java.util.UUID

class AuditDataSourceImpl(
    private val auditMapper: Mapper<String, AuditLog>,
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
            if (lines.isEmpty())  return Result.success(emptyList())

            val logs = lines.mapNotNull { line ->
                val parts = line.split(",")
                    if (parts.size >= 10) {
                        AuditLog(
                            auditId = UUID.fromString(parts[0]),
                            itemId = UUID.fromString(parts[1]),
                            itemName = parts[2],
                            userId = UUID.fromString(parts[3]),
                            editorName = parts[4],
                            actionType = AuditAction.valueOf(parts[5]),
                            auditTime = LocalDateTime.parse(parts[6]),
                            changedField = parts[7],
                            oldValue = parts[8],
                            newValue = parts[9]
                        )
                    } else null
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