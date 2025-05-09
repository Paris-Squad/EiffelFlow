package org.example.data.local.parser

import kotlinx.datetime.LocalDateTime
import org.example.data.local.csvparser.CsvParser
import org.example.data.local.utils.AuditCsvColumnIndex
import org.example.domain.model.AuditLogAction
import org.example.domain.model.AuditLog
import java.util.UUID

class AuditCsvParser : CsvParser<AuditLog> {
    override fun parseCsvLine(csvLine: String): AuditLog {
        val parts = csvLine.split(",")

        return AuditLog(
            auditId = UUID.fromString(parts[AuditCsvColumnIndex.AUDIT_ID]),
            itemId = UUID.fromString(parts[AuditCsvColumnIndex.ITEM_ID]),
            itemName = parts[AuditCsvColumnIndex.ITEM_NAME],
            userId = UUID.fromString(parts[AuditCsvColumnIndex.USER_ID]),
            editorName = parts[AuditCsvColumnIndex.USER_NAME],
            actionType = AuditLogAction.valueOf(parts[AuditCsvColumnIndex.ACTION_TYPE]),
            auditTime = LocalDateTime.parse(parts[AuditCsvColumnIndex.AUDIT_TIME]),
            changedField = parts[AuditCsvColumnIndex.CHANGED_FIELD].takeIf { it.isNotBlank() },
            oldValue = parts[AuditCsvColumnIndex.OLD_VALUE].takeIf { it.isNotBlank() },
            newValue = parts[AuditCsvColumnIndex.NEW_VALUE].takeIf { it.isNotBlank() },
        )
    }

    override fun serialize(item: AuditLog): String {
        return listOf(
            item.auditId.toString(),
            item.itemId.toString(),
            item.itemName,
            item.userId.toString(),
            item.editorName,
            item.actionType.name,
            item.auditTime.toString(),
            item.changedField ?: "",
            item.oldValue ?: "",
            item.newValue ?: ""
        ).joinToString(",")
    }

}