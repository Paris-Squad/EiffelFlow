package org.example.domain.mapper

import org.example.domain.model.AuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.Task
import org.example.domain.model.User

fun Task.toAuditLog(
    editor: User,
    actionType: AuditLogAction,
    changedField: String? = null,
    oldValue: String? = null,
    newValue: String
): AuditLog {
    return AuditLog(
        itemId = this.taskId,
        itemName = this.title,
        userId = editor.userId,
        editorName = editor.username,
        actionType = actionType,
        changedField = changedField,
        oldValue = oldValue,
        newValue = newValue
    )
}