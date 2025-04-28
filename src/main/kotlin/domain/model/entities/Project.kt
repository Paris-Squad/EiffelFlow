package org.example.domain.model.entities

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class Project(
    val projectId: UUID = UUID.randomUUID(),
    val projectName: String,
    val projectDescription: String,
    val createdAt: LocalDateTime,
    val adminId: UUID,
    val states: List<State>
)

