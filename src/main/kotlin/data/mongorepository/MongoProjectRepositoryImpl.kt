package data.mongorepository

import org.example.domain.model.Project
import org.example.domain.repository.ProjectRepository
import java.util.UUID

class MongoProjectRepositoryImpl : ProjectRepository {
    override suspend fun createProject(project: Project): Project {
        TODO("Not yet implemented")
    }

    override suspend fun updateProject(
        project: Project,
        oldProject: Project,
        changedField: String
    ): Project {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProject(projectId: UUID): Project {
        TODO("Not yet implemented")
    }

    override suspend fun getProjectById(projectId: UUID): Project {
        TODO("Not yet implemented")
    }

    override suspend fun getProjects(): List<Project> {
        TODO("Not yet implemented")
    }
}