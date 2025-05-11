package org.example.presentation.helper.extensions

import org.example.domain.model.AuditLogAction

fun AuditLogAction.toDisplayName(): String = when(this){
    AuditLogAction.CREATE -> "Created"
    AuditLogAction.UPDATE -> "Updated"
    AuditLogAction.DELETE -> "Deleted"
}