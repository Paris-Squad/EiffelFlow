package org.example.data.repository


import org.example.data.storage.FileDataSource
import org.example.data.storage.SessionManger
import org.example.data.storage.mapper.ProjectCsvMapper
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.Project
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.ProjectRepository
import java.util.UUID

class ProjectRepositoryImpl(
    private val projectMapper: ProjectCsvMapper,
    private val csvManager: FileDataSource,
    private val auditRepository: AuditRepository
) : ProjectRepository {

    override fun createProject(project: Project): Result<Project> {
        if(SessionManger.isAdmin().not()) return Result.failure(EiffelFlowException.AuthorizationException("Not Allowed"))
       return runCatching{
            val csvLine = projectMapper.mapTo(project)
            csvManager.writeLinesToFile(csvLine + "\n")
            val auditLog = project.toAuditLog(SessionManger.getUser(), actionType = AuditLogAction.CREATE)
            auditRepository.createAuditLog(auditLog)
           project
       }.recoverCatching {
           throw EiffelFlowException.IOException("Can't create project. ${it.message}")
       }
    }

    override fun updateProject(project: Project): Result<Project> {
        TODO("Not yet implemented")
    }

    override fun deleteProject(projectId: UUID): Result<Project> {
        return runCatching {
            val lines = csvManager.readLinesFromFile().toMutableList()

            val removedLine = lines.find { line ->
                val project = projectMapper.mapFrom(line)
                project.projectId == projectId
            }
                ?: return Result.failure(EiffelFlowException.IOException("Can't delete project. Project not found with ID: $projectId."))

            lines.remove(removedLine)
            csvManager.writeLinesToFile(lines.joinToString("\n"))
            val deletedProject = projectMapper.mapFrom(removedLine)

            val auditLog = deletedProject.toAuditLog(SessionManger.getUser(), actionType = AuditLogAction.CREATE)
            auditRepository.createAuditLog(auditLog)
            deletedProject
        }.recoverCatching {
            throw  EiffelFlowException.IOException("Can't delete project. Project not found with ID: $projectId, ${it.message}")
        }
    }

    override fun getProjects(): Result<List<Project>> {
        return runCatching {
            csvManager.readLinesFromFile()
                .map(projectMapper::mapFrom)
                .ifEmpty {
                    throw EiffelFlowException.NotFoundException("No projects found")
                }
        }.recoverCatching {
            throw EiffelFlowException.NotFoundException("No projects found")
        }
    }

    override fun getProjectById(projectId: UUID): Result<Project> {
        return runCatching {
            csvManager.readLinesFromFile()
                .map(projectMapper::mapFrom)
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