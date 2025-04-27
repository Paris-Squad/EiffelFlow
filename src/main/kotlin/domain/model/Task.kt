package org.example.domain.model

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class Task(
    val taskId: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    val createdAt: LocalDateTime,
    val creatorId: UUID,
    val projectId: UUID,
    val stateId: UUID,
    val assignedId: UUID,
    val role: RoleType
)
