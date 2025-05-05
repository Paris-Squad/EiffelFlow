package org.example.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID

data class Project(
    val projectId: UUID = UUID.randomUUID(),
    val projectName: String,
    val projectDescription: String,
    val createdAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val adminId: UUID,
    val taskStates: List<TaskState> = emptyList()
)
