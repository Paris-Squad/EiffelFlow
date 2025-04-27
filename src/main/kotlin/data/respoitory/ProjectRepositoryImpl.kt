package org.example.data.respoitory

import org.example.domain.model.Project
import org.example.domain.repository.ProjectRepository
import org.example.data.storge.DataSource
import java.util.UUID

class ProjectRepositoryImpl(
    private val dataSource: DataSource<Project>
): ProjectRepository {
    override fun createProject(project: Project): Project {
        TODO("Not yet implemented")
    }

    override fun updateProject(project: Project): Project {
        TODO("Not yet implemented")
    }

    override fun deleteProject(projectId: UUID): Project {
        TODO("Not yet implemented")
    }

    override fun getProjects(): List<Project> {
        TODO("Not yet implemented")
    }
}