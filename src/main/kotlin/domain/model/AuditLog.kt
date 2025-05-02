package org.example.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID

data class AuditLog(
    val auditId: UUID = UUID.randomUUID(),
    val itemId: UUID,
    val itemName: String,
    val userId: UUID,
    val editorName: String,
    val actionType: AuditLogAction,
    val auditTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val changedField: String?,
    val oldValue: String?,
    val newValue: String?,
)


