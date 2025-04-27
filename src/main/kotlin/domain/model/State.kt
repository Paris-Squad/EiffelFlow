package org.example.domain.model

import java.util.UUID

data class State(
    val stateId: UUID = UUID.randomUUID(),
    val name: String
)


