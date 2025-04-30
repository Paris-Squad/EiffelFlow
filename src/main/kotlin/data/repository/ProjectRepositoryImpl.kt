package org.example.data.repository

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.common.Constants.PROJECT
import org.example.common.Constants.USER
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

        val createdProject = projectDataSource.createProject(project)

        return createdProject.fold(
            onSuccess = { createdProject ->
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

                return auditDataSource.createAuditLog(auditLog).fold(
                    onSuccess = { Result.success(createdProject) },
                    onFailure = { Result.failure(it) }
                )
            },
            onFailure = { throwable ->
                Result.failure(throwable)
            }
        )
    }

    override fun updateProject(project: Project): Result<Project> {

        val currentProjectData=getProjectById(project.projectId)

        return projectDataSource.updateProject(project).also { result ->
            result.onSuccess { updatedProject ->

                val auditLog = AuditLog(
                    itemId = updatedProject.projectId,
                    itemName = PROJECT,
                    userId = updatedProject.adminId,
                    userName = "Admin",
                    actionType = AuditAction.CREATE,
                    auditTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    changedField = null,
                    oldValue = null,
                    newValue = updatedProject.toString()
                )
                auditDataSource.createAuditLog(auditLog)
            }
        }
    }

    private fun getChangedField(oldProject: Project,newProject: Project){

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

    companion object {
        private val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
}