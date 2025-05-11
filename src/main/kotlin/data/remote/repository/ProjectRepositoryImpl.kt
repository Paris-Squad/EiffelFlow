package data.remote.repository

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.BaseRepository
import org.example.data.remote.MongoCollections
import org.example.data.remote.dto.MongoProjectDto
import org.example.data.remote.mapper.ProjectMapper
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.model.TaskState
import org.example.domain.repository.ProjectRepository
import java.util.UUID

class ProjectRepositoryImpl(
    database: MongoDatabase,
    private val projectMapper: ProjectMapper
) : BaseRepository(), ProjectRepository {

    private val projectsCollection = database.getCollection<MongoProjectDto>(collectionName = MongoCollections.PROJECTS)

    override suspend fun createProject(project: Project): Project {
        return wrapInTryCatch {
            if (project.taskStates.isEmpty()){
                val defaultState = TaskState(name = "To Do")
                project.taskStates = listOf(defaultState)
            }
            val projectDto = projectMapper.toDto(project)
            projectsCollection.insertOne(projectDto)
            project
        }
    }

    override suspend fun updateProject(
        project: Project,
        oldProject: Project,
        changedField: String
    ): Project {
        return wrapInTryCatch {
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

            oldUser ?: throw EiffelFlowException.NotFoundException("Project with id ${project.projectId} not found")

            project
        }
    }

    override suspend fun deleteProject(projectId: UUID): Project {
        return wrapInTryCatch {
            val query = eq(MongoProjectDto::_id.name, projectId.toString())
            val deletedProjectDto = projectsCollection.findOneAndDelete(query)
            if (deletedProjectDto == null) {
                throw EiffelFlowException.NotFoundException("Project with id $projectId not found")
            }
            val deletedProject = projectMapper.fromDto(deletedProjectDto)
            deletedProject
        }
    }

    override suspend fun getProjectById(projectId: UUID): Project {
        return wrapInTryCatch {
            val query = eq(MongoProjectDto::_id.name, projectId.toString())
            val projectDto = projectsCollection.find(query).firstOrNull()
            projectDto ?: throw EiffelFlowException.NotFoundException("Project with id $projectId not found")
            val project = projectMapper.fromDto(projectDto)
            project
        }
    }

    override suspend fun getProjects(): List<Project> {
        return wrapInTryCatch {
            val projectsDto = projectsCollection.find().toList()
            projectsDto.map { projectMapper.fromDto(it) }
        }
    }

}