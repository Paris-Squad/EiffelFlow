package org.example.domain.utils

import org.example.domain.model.User

fun User.getChangedFieldNames(updatedUser: User): Set<String> {
    val changedFields = mutableSetOf<String>()

    if (this.username != updatedUser.username) changedFields.add("username")
    if (this.password != updatedUser.password) changedFields.add("password")
    if (this.role != updatedUser.role) changedFields.add("role")

    return changedFields
}