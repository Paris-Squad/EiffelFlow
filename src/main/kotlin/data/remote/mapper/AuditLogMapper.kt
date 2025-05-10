package org.example.data.remote.mapper

import kotlinx.datetime.LocalDateTime
import org.example.data.remote.dto.MongoAuditLogDto
import org.example.domain.model.AuditLog
import org.example.domain.model.AuditLogAction
import java.util.UUID

class AuditLogMapper : Mapper<MongoAuditLogDto, AuditLog> {

    override fun toDto(entity: AuditLog) = MongoAuditLogDto(
        _id = entity.auditId.toString(),
        itemId = entity.itemId.toString(),
        itemName = entity.itemName,
        userId = entity.userId.toString(),
        editorName = entity.editorName,
        actionType = entity.actionType.name,
        auditTime = entity.auditTime.toString(),
        changedField = entity.changedField,
        oldValue = entity.oldValue,
        newValue = entity.newValue
    )

    override fun fromDto(dto: MongoAuditLogDto) = AuditLog(
        auditId = UUID.fromString(dto._id),
        itemId = UUID.fromString(dto.itemId),
        itemName = dto.itemName,
        userId = UUID.fromString(dto.userId),
        editorName = dto.editorName,
        actionType = AuditLogAction.valueOf(dto.actionType),
        auditTime = LocalDateTime.parse(dto.auditTime),
        changedField = dto.changedField,
        oldValue = dto.oldValue,
        newValue = dto.newValue
    )
}
