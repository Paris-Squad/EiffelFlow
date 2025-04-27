package org.example.domain.model

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class AuditLog(
    val auditId: UUID = UUID.randomUUID(),
    val itemId: UUID,
    val userId: UUID,
    val actionType: ActionType,
    val auditTime: LocalDateTime,
    val changedField: String?,
    val oldValue: String?,
    val newValue: String?,
)


