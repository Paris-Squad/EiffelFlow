package org.example.domain.mapper

import org.example.domain.model.AuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.User
import java.util.UUID

fun User.toAuditLog(
    editor: User,
    actionType: AuditLogAction,
    changedField: String? = null,
    oldValue: String? = null,
    newValue: String = this.toString()
): AuditLog {
    return AuditLog(
        auditId = UUID.randomUUID(),
        itemId = this.userId,
        itemName = this.username,
        userId = editor.userId,
        editorName = editor.username,
        actionType = actionType,
        changedField = changedField,
        oldValue = oldValue,
        newValue = newValue
    )
}