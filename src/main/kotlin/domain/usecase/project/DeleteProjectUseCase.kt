package org.example.domain.usecase.project

import org.example.data.utils.SessionManger
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.Project
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.ProjectRepository
import java.util.UUID

class DeleteProjectUseCase(
    private val projectRepository: ProjectRepository ,
    private val auditRepository: AuditRepository
) {

    suspend fun deleteProject(projectId: UUID):Project {

        val deletedProject = projectRepository.deleteProject(projectId)

        val auditLog = deletedProject.toAuditLog(
            editor = SessionManger.getUser(),
            actionType = AuditLogAction.DELETE,
            newValue = deletedProject.projectName
        )

        auditRepository.createAuditLog(auditLog)

        return deletedProject
    }

}