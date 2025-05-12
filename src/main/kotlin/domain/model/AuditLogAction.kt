package org.example.domain.model

enum class AuditLogAction(val actionName: String) {
    CREATE("Created"),
    UPDATE("Updated"),
    DELETE("Deleted")
}

