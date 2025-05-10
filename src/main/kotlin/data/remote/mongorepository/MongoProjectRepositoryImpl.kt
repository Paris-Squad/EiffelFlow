package data.mongorepository

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.remote.MongoCollections
import org.example.data.remote.dto.MongoProjectDto
import org.example.data.remote.mapper.ProjectMapper
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.repository.ProjectRepository
import java.util.UUID

class MongoProjectRepositoryImpl(
    database: MongoDatabase,
    private val projectMapper: ProjectMapper
) : ProjectRepository {

    private val projectsCollection = database.getCollection<MongoProjectDto>(collectionName = MongoCollections.PROJECTS)

    override suspend fun createProject(project: Project): Project {
        try {
            val projectDto = projectMapper.toDto(project)
            projectsCollection.insertOne(projectDto)

            return project
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't create Project because ${exception.message}")
        }
    }

    override suspend fun updateProject(
        project: Project,
        oldProject: Project,
        changedField: String
    ): Project {
        try {
            val projectDto = projectMapper.toDto(project)
            val updates = Updates.combine(
                Updates.set(MongoProjectDto::projectName.name, projectDto.projectName),
                Updates.set(MongoProjectDto::projectDescription.name, projectDto.projectDescription),
                Updates.set(MongoProjectDto::adminId.name, projectDto.adminId),
                Updates.set(MongoProjectDto::taskStates.name, projectDto.taskStates),
            )

            val options = FindOneAndUpdateOptions().upsert(false)
            val query = eq(MongoProjectDto::_id.name, project.projectId.toString())
            val oldUser = projectsCollection.findOneAndUpdate(query, updates, options)

            if (oldUser == null) {
                throw EiffelFlowException.NotFoundException("Project with id ${project.projectId} not found")
            }

            return project
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't update Project with id ${project.projectId} because ${exception.message}")
        }
    }

    override suspend fun deleteProject(projectId: UUID): Project {
        try {
            val query = eq(MongoProjectDto::_id.name, projectId.toString())
            val deletedProjectDto = projectsCollection.findOneAndDelete(query)
            if (deletedProjectDto == null) {
                throw EiffelFlowException.NotFoundException("Project with id $projectId not found")
            }
            val deletedProject = projectMapper.fromDto(deletedProjectDto)
            return deletedProject

        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't delete Project with id $projectId because ${exception.message}")
        }
    }

    override suspend fun getProjectById(projectId: UUID): Project {
        require(SessionManger.isAdmin()) {
            throw EiffelFlowException.AuthorizationException("Not Allowed, Admin only allowed to create project")
        }
        try {
            val query = eq(MongoProjectDto::_id.name, projectId.toString())
            val projectDto = projectsCollection.find(query).firstOrNull()
            projectDto ?: throw EiffelFlowException.NotFoundException("Project with id $projectId not found")
            val project = projectMapper.fromDto(projectDto)
            return project

        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't get Project with id $projectId because ${exception.message}")
        }
    }

    override suspend fun getProjects(): List<Project> {
        require(SessionManger.isAdmin()) {
            throw EiffelFlowException.AuthorizationException("Not Allowed, Admin only allowed to create project")
        }
        try {
            val projectsDto = projectsCollection.find().toList()
            return projectsDto.map { projectMapper.fromDto(it) }
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't get Projects because ${exception.message}")
        }
    }

}