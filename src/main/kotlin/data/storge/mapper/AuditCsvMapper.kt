package org.example.data.storge.mapper

import kotlinx.datetime.LocalDateTime
import org.example.data.storge.Mapper
import org.example.data.utils.AuditCsvColumnIndex
import org.example.domain.model.entities.AuditAction
import org.example.domain.model.entities.AuditLog
import java.util.UUID

class AuditCsvMapper : Mapper<String, AuditLog> {
    override fun mapFrom(input: String): AuditLog {
        val parts = input.split(",")

        return AuditLog(
            auditId = UUID.fromString(parts[AuditCsvColumnIndex.AUDIT_ID]),
            itemId = UUID.fromString(parts[AuditCsvColumnIndex.ITEM_ID]),
            itemName = parts[AuditCsvColumnIndex.ITEM_NAME],
            userId = UUID.fromString(parts[AuditCsvColumnIndex.USER_ID]),
            userName = parts[AuditCsvColumnIndex.USER_NAME],
            actionType = AuditAction.valueOf(parts[AuditCsvColumnIndex.ACTION_TYPE]),
            auditTime = LocalDateTime.parse(parts[AuditCsvColumnIndex.AUDIT_TIME]),
            changedField = parts.getOrNull(AuditCsvColumnIndex.CHANGED_FIELD)?.takeIf { it.isNotBlank() },
            oldValue = parts.getOrNull(AuditCsvColumnIndex.OLD_VALUE)?.takeIf { it.isNotBlank() },
            newValue = parts.getOrNull(AuditCsvColumnIndex.NEW_VALUE)?.takeIf { it.isNotBlank() },
        )
    }

    override fun mapTo(output: AuditLog): String {
        return listOf(
            output.auditId.toString(),
            output.itemId.toString(),
            output.itemName,
            output.userId.toString(),
            output.userName,
            output.actionType.name,
            output.auditTime.toString(),
            output.changedField ?: "",
            output.oldValue ?: "",
            output.newValue ?: ""
        ).joinToString(",")
    }
}