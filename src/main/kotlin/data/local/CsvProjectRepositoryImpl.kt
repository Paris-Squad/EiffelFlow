package org.example.data.local


import org.example.data.storage.FileDataSource
import org.example.data.storage.SessionManger
import org.example.data.storage.parser.ProjectCsvParser
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.Project
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.ProjectRepository
import java.util.UUID
import kotlin.jvm.Throws

class CsvProjectRepositoryImpl(
    private val projectCsvParser: ProjectCsvParser,
    private val fileDataSource: FileDataSource,
    private val auditRepository: AuditRepository
) : ProjectRepository {

    @Throws(EiffelFlowException::class)
    override suspend fun createProject(project: Project): Project {

        if (SessionManger.isAdmin().not()) {
            throw EiffelFlowException.AuthorizationException("Not Allowed, Admin only allowed to create project")
        }

        return try {
            val csvLine = projectCsvParser.serialize(project)
            fileDataSource.writeLinesToFile(csvLine)
            val auditLog = project.toAuditLog(
                editor = SessionManger.getUser(),
                actionType = AuditLogAction.CREATE,
                newValue = project.projectName
            )
            auditRepository.createAuditLog(auditLog)
            project
        } catch (throwable: Throwable) {
            throw EiffelFlowException.IOException("Can't create project. ${throwable.message}")
        }
    }

    @Throws(EiffelFlowException::class)
    override suspend fun updateProject(project: Project, oldProject: Project, changedField: String): Project {
        if (SessionManger.isAdmin().not()) {
            throw EiffelFlowException.AuthorizationException("Not Allowed, Admin only allowed to update project")
        }

        return try {
            val projectCsv = projectCsvParser.serialize(project)
            val oldProjectCsv = projectCsvParser.serialize(oldProject)
            fileDataSource.updateLinesToFile(projectCsv, oldProjectCsv)

            val auditLog = project.toAuditLog(
                editor = SessionManger.getUser(),
                actionType = AuditLogAction.UPDATE,
                changedField = changedField,
                oldValue = oldProject.toString(),
                newValue = project.toString()
            )
            auditRepository.createAuditLog(auditLog)

            project
        } catch (throwable: Throwable) {
            throw EiffelFlowException.IOException("Can't update project. ${throwable.message}")
        }
    }

    @Throws(EiffelFlowException::class)
    override suspend fun deleteProject(projectId: UUID): Project {
        if (SessionManger.isAdmin().not()) {
            throw EiffelFlowException.AuthorizationException("Not Allowed, Admin only allowed to delete project")
        }

        return try {

            val projects = getProjects()

            val projectToDelete = projects.find { it.projectId == projectId }
                ?: throw EiffelFlowException.NotFoundException("Project not found")

            val projectCsv = projectCsvParser.serialize(projectToDelete)
            fileDataSource.deleteLineFromFile(projectCsv)

            val auditLog = projectToDelete.toAuditLog(
                editor = SessionManger.getUser(),
                actionType = AuditLogAction.CREATE,
                newValue = projectToDelete.projectName
            )
            auditRepository.createAuditLog(auditLog)
            projectToDelete
        } catch (throwable: Throwable) {
            throw EiffelFlowException.IOException(
                "Can't delete project. Project not found with ID: $projectId, ${throwable.message}"
            )
        }
    }

    @Throws(EiffelFlowException::class)
    override suspend fun getProjects(): List<Project> {
        return try {
            fileDataSource.readLinesFromFile()
                .map(projectCsvParser::parseCsvLine)
        } catch (throwable: Throwable) {
            throw EiffelFlowException.NotFoundException("No projects found, ${throwable.message}")
        }
    }

    @Throws(EiffelFlowException::class)
    override suspend fun getProjectById(projectId: UUID): Project {
        return try {
            fileDataSource.readLinesFromFile()
                .map(projectCsvParser::parseCsvLine)
                .firstOrNull { it.projectId == projectId }
                ?: throw EiffelFlowException.NotFoundException("Project not found")
        } catch (throwable: Throwable) {
            throw EiffelFlowException.NotFoundException("Project not found, ${throwable.message}")
        }
    }

    companion object {
        const val FILE_NAME: String = "projects.csv"
    }
}