package org.example.domain.model.entities

import java.util.UUID

data class User(
    val userId: UUID = UUID.randomUUID(),
    val username: String,
    val password: String,
    val role: RoleType
)
