package org.example.domain.usecase.project

import org.example.data.utils.SessionManger
import org.example.domain.model.Project
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.ProjectRepository
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction


class CreateProjectUseCase(
    private val repository: ProjectRepository ,
    private val auditRepository: AuditRepository
) {
    suspend fun createProject(project : Project) : Project {
        if (!SessionManger.isAdmin()) {
            throw EiffelFlowException.AuthorizationException("Not Allowed, Admin only allowed to create project")
        }

        val createdProject = repository.createProject(project)

        val auditLog = createdProject.toAuditLog(
            editor = SessionManger.getUser(),
            actionType = AuditLogAction.CREATE,
            newValue = createdProject.projectName
        )
        auditRepository.createAuditLog(auditLog)

        return createdProject

    }
}
