package org.example.data.respoitory

import org.example.data.storge.audit.AuditDataSource
import org.example.data.storge.project.ProjectDataSource
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
        TODO("Not yet implemented")
    }

    override fun getProjectById(projectID: UUID): Result<Project> {
        TODO("Not yet implemented")
    }

    override fun getProjects(): Result<List<Project>> {
        TODO("Not yet implemented")
    }
}