package org.example.domain.model

import java.util.UUID

data class TaskState(
    val stateId: UUID = UUID.randomUUID(),
    val name: String
)


