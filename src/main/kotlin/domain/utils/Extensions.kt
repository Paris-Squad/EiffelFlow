package org.example.domain.utils

import org.example.domain.model.FieldChange
import org.example.domain.model.User

fun User.getFieldChanges(updatedUser: User): Set<FieldChange> {
    val changedFields = mutableSetOf<FieldChange>()

    if (this.username != updatedUser.username) {
        changedFields.add(FieldChange("username", this.username, updatedUser.username))
    }
    if (this.password != updatedUser.password) {
        changedFields.add(FieldChange("password", this.password, updatedUser.password))
    }
    if (this.role != updatedUser.role) {
        changedFields.add(FieldChange("role", this.role.name, updatedUser.role.name))
    }
    return changedFields
}