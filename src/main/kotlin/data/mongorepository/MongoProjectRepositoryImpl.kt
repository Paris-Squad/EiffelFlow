package data.mongorepository

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.example.data.MongoCollections
import org.example.data.storage.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.Project
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.ProjectRepository
import org.example.domain.utils.getFieldChanges
import java.util.UUID

class MongoProjectRepositoryImpl(
    database: MongoDatabase,
    private val auditRepository: AuditRepository
) : ProjectRepository {

    private val projectsCollection = database.getCollection<Project>(collectionName = MongoCollections.PROJECTS)

    override suspend fun createProject(project: Project): Project {
        require(SessionManger.isAdmin()) {
            throw EiffelFlowException.AuthorizationException("Not Allowed, Admin only allowed to create project")
        }
        try {
            val existingProject = projectsCollection.find(eq("projectId", project.projectId)).firstOrNull()
            if (existingProject != null) {
                throw EiffelFlowException.IOException("Project with projectId ${project.projectId} already exists")
            }
            projectsCollection.insertOne(project)

            logAction(project, AuditLogAction.CREATE)

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
        require(SessionManger.isAdmin()) {
            throw EiffelFlowException.AuthorizationException("Not Allowed, Admin only allowed to create project")
        }

        try {
            val updates = Updates.combine(
                Updates.set(Project::projectName.name, project.projectName),
                Updates.set(Project::projectDescription.name, project.projectDescription),
                Updates.set(Project::adminId.name, project.adminId),
                Updates.set(Project::taskStates.name, project.taskStates),
            )

            val options = FindOneAndUpdateOptions().upsert(false)
            val query = eq("projectId", project.projectId)
            val oldUser = projectsCollection.findOneAndUpdate(query, updates, options)

            if (oldUser == null) {
                throw EiffelFlowException.NotFoundException("Project with id ${project.projectId} not found")
            }

            val fieldChanges = oldProject.getFieldChanges(project)
            val changedFieldsNames = fieldChanges.map { it.fieldName }
            val oldValues = fieldChanges.map { it.oldValue }
            val newValues = fieldChanges.map { it.newValue }
            logAction(
                project = project,
                actionType = AuditLogAction.UPDATE,
                changedField = changedFieldsNames.toString(),
                oldValue = oldValues.toString(),
                newValue = newValues.toString(),
            )
            return project
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't update Project with id ${project.projectId} because ${exception.message}")
        }
    }

    override suspend fun deleteProject(projectId: UUID): Project {
        require(SessionManger.isAdmin()) {
            throw EiffelFlowException.AuthorizationException("Not Allowed, Admin only allowed to create project")
        }
        try {
            val query = eq("projectId", projectId)
            val deletedProject = projectsCollection.findOneAndDelete(query)

            if (deletedProject == null) {
                throw EiffelFlowException.NotFoundException("Project with id $projectId not found")
            }
            logAction(
                project = deletedProject,
                actionType = AuditLogAction.DELETE
            )
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
            val project = projectsCollection.find(eq("projectId", projectId)).firstOrNull()
            return project ?: throw EiffelFlowException.NotFoundException("Project with id $projectId not found")
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't get Project with id $projectId because ${exception.message}")
        }
    }

    override suspend fun getProjects(): List<Project> {
        require(SessionManger.isAdmin()) {
            throw EiffelFlowException.AuthorizationException("Not Allowed, Admin only allowed to create project")
        }
        try {
            val projects = mutableListOf<Project>()
            projectsCollection.find().collect { project ->
                projects.add(project)
            }
            return projects
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't get Projects because ${exception.message}")
        }
    }

    private suspend fun logAction(
        project: Project,
        actionType: AuditLogAction,
        changedField: String? = null,
        oldValue: String? = null,
        newValue: String = project.toString()
    ) {
        val auditLog = project.toAuditLog(
            editor = SessionManger.getUser(),
            actionType = actionType,
            changedField = changedField,
            oldValue = oldValue,
            newValue = newValue,
        )
        auditRepository.createAuditLog(auditLog)
    }
}