package org.example.data.repository


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

class ProjectRepositoryImpl(
    private val projectCsvParser: ProjectCsvParser,
    private val fileDataSource: FileDataSource,
    private val auditRepository: AuditRepository
) : ProjectRepository {

    override fun createProject(project: Project): Result<Project> {
        if(SessionManger.isAdmin().not()) return Result.failure(EiffelFlowException.AuthorizationException("Not Allowed"))
       return runCatching{
            val csvLine = projectCsvParser.serialize(project)
            fileDataSource.writeLinesToFile(csvLine + "\n")
            val auditLog = project.toAuditLog(
                editor = SessionManger.getUser(),
                actionType = AuditLogAction.CREATE,
                newValue = project.projectName)
            auditRepository.createAuditLog(auditLog)
           project
       }.recoverCatching {
           throw EiffelFlowException.IOException("Can't create project. ${it.message}")
       }
    }

    override fun updateProject(project: Project ,oldProject: Project ,changedField: String): Result<Project> {
        if(SessionManger.isAdmin().not()) return Result.failure(EiffelFlowException.AuthorizationException("Not Allowed"))
        return runCatching {
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
        }.recoverCatching {
            throw EiffelFlowException.IOException("Can't update project. ${it.message}")
        }
    }

    override fun deleteProject(projectId: UUID): Result<Project> {
        if(SessionManger.isAdmin().not())
            return Result.failure(EiffelFlowException.AuthorizationException("Not Allowed"))

        return runCatching {
            val lines = fileDataSource.readLinesFromFile().toMutableList()

            val removedLine = lines.find { line ->
                val project = projectCsvParser.parseCsvLine(line)
                project.projectId == projectId
            }
                ?: return Result.failure(
                    EiffelFlowException.IOException(
                        "Can't delete project. Project not found with ID: $projectId."
                    )
                )
            lines.remove(removedLine)
            fileDataSource.writeLinesToFile(lines.joinToString("\n"))
            val deletedProject = projectCsvParser.parseCsvLine(removedLine)

            val auditLog = deletedProject.toAuditLog(
                editor = SessionManger.getUser(),
                actionType = AuditLogAction.CREATE,
                newValue = deletedProject.projectName)
            auditRepository.createAuditLog(auditLog)
            deletedProject
        }.recoverCatching {
            throw  EiffelFlowException.IOException(
                "Can't delete project. Project not found with ID: $projectId, ${it.message}"
            )
        }
    }

    override fun getProjects(): Result<List<Project>> {
        return runCatching {
            fileDataSource.readLinesFromFile()
                .map(projectCsvParser::parseCsvLine)
                .ifEmpty {
                    throw EiffelFlowException.NotFoundException("No projects found")
                }
        }.recoverCatching {
            throw EiffelFlowException.NotFoundException("No projects found")
        }
    }

    override fun getProjectById(projectId: UUID): Result<Project> {
        return runCatching {
            fileDataSource.readLinesFromFile()
                .map(projectCsvParser::parseCsvLine)
                .firstOrNull { it.projectId == projectId }
                ?: throw EiffelFlowException.NotFoundException("Project not found")
        }.recoverCatching {
            throw EiffelFlowException.NotFoundException("Project not found")
        }
    }

    companion object {
        const val FILE_NAME: String = "projects.csv"
    }
}