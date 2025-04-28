package org.example.domain.model.entities

import java.util.UUID

data class State(
    val stateId: UUID = UUID.randomUUID(),
    val name: String
)


