package org.example.data.respoitory

import org.example.data.storge.audit.AuditDataSource
import org.example.data.storge.project.ProjectDataSource
import org.example.domain.model.entities.AuditLog
import org.example.domain.model.entities.Project
import org.example.domain.repository.ProjectRepository
import java.util.UUID
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.domain.model.entities.AuditAction

class ProjectRepositoryImpl(
    private val projectDataSource: ProjectDataSource,
    private val auditDataSource: AuditDataSource
) : ProjectRepository {

    override fun createProject(project: Project): Result<Project> {
        val createdProject = projectDataSource.createProject(project)

        return createdProject.onSuccess { createdProject ->
            val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            val auditLog = AuditLog(
                auditId = UUID.randomUUID(),
                itemId = createdProject.projectId,
                itemName = createdProject.projectName,
                userId = createdProject.adminId,
                userName = "Admin",
                actionType = AuditAction.CREATE,
                auditTime = currentTime,
                changedField = null,
                oldValue = null,
                newValue = createdProject.projectName
            )

            auditDataSource.createAuditLog(auditLog).onSuccess {
                Result.success(createdProject)
            }.onFailure { throwable ->
                return Result.failure(throwable)
            }

        }.onFailure {
            return Result.failure(it)
        }
    }

    override fun updateProject(project: Project): Result<Project> {
        TODO("Not yet implemented")
    }

    override fun deleteProject(projectId: UUID): Result<Project> {
        TODO("Not yet implemented")
    }

    override fun getProjectById(projectID: UUID): Result<Project> {
        TODO("Not yet implemented")
    }

    override fun getProjects(): Result<List<Project>> {
        TODO("Not yet implemented")
    }
}