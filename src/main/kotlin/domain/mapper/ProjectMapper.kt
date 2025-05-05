package org.example.domain.mapper

import org.example.domain.model.AuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.Project
import org.example.domain.model.User
import java.util.UUID

fun Project.toAuditLog(
    editor: User,
    actionType: AuditLogAction,
    changedField: String? = null,
    oldValue: String? = null,
    newValue: String
): AuditLog {
    return AuditLog(
        auditId = UUID.randomUUID(),
        itemId = this.projectId,
        itemName = this.projectName,
        userId = editor.userId,
        editorName = editor.username,
        actionType = actionType,
        changedField = changedField,
        oldValue = oldValue,
        newValue = newValue
    )
}