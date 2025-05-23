package org.example.domain.utils

import org.example.domain.model.FieldChange
import org.example.domain.model.Project
import org.example.domain.model.Task
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

fun Project.getFieldChanges(updatedUser: Project): Set<FieldChange> {
    val changedFields = mutableSetOf<FieldChange>()

    if (this.projectName != updatedUser.projectName) {
        changedFields.add(FieldChange("projectName", this.projectName, updatedUser.projectName))
    }
    if (this.projectDescription != updatedUser.projectDescription) {
        changedFields.add(FieldChange("projectDescription", this.projectDescription, updatedUser.projectDescription))
    }
    if (this.adminId != updatedUser.adminId) {
        changedFields.add(FieldChange("adminId", this.adminId.toString(), updatedUser.adminId.toString()))
    }
    if (this.taskStates != updatedUser.taskStates) {
        changedFields.add(FieldChange("taskStates", this.taskStates.toString(), updatedUser.taskStates.toString()))
    }
    return changedFields
}

fun Task.getFieldChanges(updatedUser: Task): Set<FieldChange> {
    val changedFields = mutableSetOf<FieldChange>()
    if (this.title != updatedUser.title) {
        changedFields.add(FieldChange("title", this.title, updatedUser.title))
    }
    if (this.description != updatedUser.description) {
        changedFields.add(FieldChange("description", this.description, updatedUser.description))
    }
    if (this.creatorId != updatedUser.creatorId) {
        changedFields.add(FieldChange("creatorId", this.creatorId.toString(), updatedUser.creatorId.toString()))
    }
    if (this.projectId != updatedUser.projectId) {
        changedFields.add(FieldChange("projectId", this.projectId.toString(), updatedUser.projectId.toString()))
    }
    if (this.assignedId != updatedUser.assignedId) {
        changedFields.add(FieldChange("assignedId", this.assignedId.toString(), updatedUser.assignedId.toString()))
    }
    if (this.state != updatedUser.state) {
        changedFields.add(FieldChange("state", this.state.toString(), updatedUser.state.toString()))
    }
    if (this.role != updatedUser.role) {
        changedFields.add(FieldChange("role", this.role.toString(), updatedUser.role.toString()))
    }
    return changedFields
}