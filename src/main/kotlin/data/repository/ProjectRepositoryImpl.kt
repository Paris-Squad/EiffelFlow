package org.example.data.repository

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.data.storage.audit.AuditDataSource
import org.example.data.storage.project.ProjectDataSource
import org.example.domain.model.entities.AuditAction
import org.example.domain.model.entities.AuditLog
import org.example.domain.model.entities.Project
import org.example.domain.repository.ProjectRepository
import java.util.UUID

class ProjectRepositoryImpl(
    private val projectDataSource: ProjectDataSource,
    private val auditDataSource: AuditDataSource
) : ProjectRepository {

    override fun createProject(project: Project): Result<Project> {
        TODO("Not yet implemented")
    }

    override fun updateProject(project: Project): Result<Project> {
        TODO("Not yet implemented")
    }

    override fun deleteProject(projectId: UUID): Result<Project> {
        val deletedProject = projectDataSource.deleteProject(projectId)

        return deletedProject.fold(
            onSuccess = {project->
                val auditLog = AuditLog(
                    auditId = UUID.randomUUID(),
                    itemId = project.projectId,
                    itemName = project.projectName,
                    userId = project.adminId,
                    userName = "Admin",
                    actionType = AuditAction.DELETE,
                    auditTime = currentTime,
                    changedField = null,
                    oldValue = null,
                    newValue = project.projectName
                )
                return auditDataSource.createAuditLog(auditLog).fold(
                    onSuccess = {
                        Result.success(project)
                    },
                    onFailure = {
                        Result.failure(it)
                    }
                )
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }

    override fun getProjectById(projectID: UUID): Result<Project> {
        TODO("Not yet implemented")
    }

    override fun getProjects(): Result<List<Project>> {
        TODO("Not yet implemented")
    }

    companion object{
        private val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
}