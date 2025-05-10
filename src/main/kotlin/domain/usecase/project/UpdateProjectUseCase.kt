package org.example.domain.usecase.project

import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.Project
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.ProjectRepository

class UpdateProjectUseCase(
    private val repository: ProjectRepository ,
    private val auditRepository: AuditRepository
) {

    @Throws(EiffelFlowException::class)
    suspend fun updateProject(updatedProject: Project): Project {

        if (SessionManger.isAdmin().not()) {
            throw EiffelFlowException.AuthorizationException("Not Allowed, Admin only allowed to update project")
        }

        val project = repository.getProjectById(updatedProject.projectId)

        if (project == updatedProject) {
            throw EiffelFlowException.IOException("No changes detected")
        }

        val changedField = detectChangedField(project, updatedProject)
        val updated = repository.updateProject(
            project = updatedProject,
            oldProject = project,
            changedField = changedField
        )
        val auditLog = updated.toAuditLog(
            editor = SessionManger.getUser(),
            actionType = AuditLogAction.UPDATE,
            changedField = changedField,
            oldValue = project.toString(),
            newValue = updated.toString()
        )
        auditRepository.createAuditLog(auditLog)
        return updated
    }

    private fun detectChangedField(original: Project, updated: Project): String {
        val changes = mutableListOf<String>()
        if (original.projectName != updated.projectName) changes.add("PROJECT_NAME")
        if (original.projectDescription != updated.projectDescription) changes.add("PROJECT_DESCRIPTION")
        if (original.adminId != updated.adminId) changes.add("ADMIN_ID")
        if (original.taskStates != updated.taskStates) changes.add("TASK_STATES")

        return changes.joinToString(", ") { it }
    }
}