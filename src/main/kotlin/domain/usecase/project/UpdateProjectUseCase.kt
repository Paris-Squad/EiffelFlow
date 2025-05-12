package org.example.domain.usecase.project

import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.Project
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.ProjectRepository
import org.example.domain.utils.getFieldChanges

class UpdateProjectUseCase(
    private val repository: ProjectRepository ,
    private val auditRepository: AuditRepository
) {

    @Throws(EiffelFlowException::class)
    suspend fun updateProject(updatedProject: Project): Project {

        val project = repository.getProjectById(updatedProject.projectId)

        val changedFields = project.getFieldChanges(updatedProject)

        if (changedFields.isEmpty()) {
            throw EiffelFlowException.IOException("No changes detected")
        }

        val changedField = changedFields.joinToString(", ") { it.fieldName }

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

}