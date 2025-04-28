package org.example.domain.model.entities

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class AuditLog(
    val auditId: UUID = UUID.randomUUID(),
    val itemId: UUID,
    val itemName: String,
    val userId: UUID,
    val userName: String,
    val actionType: AuditAction,
    val auditTime: LocalDateTime,
    val changedField: String?,
    val oldValue: String?,
    val newValue: String?,
)


