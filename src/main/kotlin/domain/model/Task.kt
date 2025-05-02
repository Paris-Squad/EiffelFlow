package org.example.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID

data class Task(
    val taskId: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    val createdAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val creatorId: UUID,
    val projectId: UUID,
    val assignedId: UUID,
    val state: TaskState,
    val role: RoleType
)
