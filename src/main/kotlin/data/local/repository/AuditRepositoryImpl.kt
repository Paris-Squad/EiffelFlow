package org.example.data.local.csvrepository

import org.example.data.BaseRepository
import org.example.data.local.FileDataSource
import org.example.data.local.parser.AuditCsvParser
import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import java.util.UUID

class AuditRepositoryImpl(
    private val auditCsvParser: AuditCsvParser,
    private val fileDataSource: FileDataSource,
    taskRepositoryProvider: Lazy<TaskRepository>
) : BaseRepository(), AuditRepository {
    private val taskRepository: TaskRepository by taskRepositoryProvider

    override suspend fun createAuditLog(auditLog: AuditLog): AuditLog {
        return executeSafely {
            val line = auditCsvParser.serialize(auditLog)
            fileDataSource.writeLinesToFile(line)
            auditLog
        }
    }

    override suspend fun getTaskAuditLogById(taskId: UUID): List<AuditLog> {
        return executeSafely {
            getAuditLogs().filter { it.itemId == taskId }.sortedByDescending { it.auditTime }
        }
    }

    override suspend fun getProjectAuditLogById(projectId: UUID): List<AuditLog> {
        return executeSafely {
            val auditLogs = getAuditLogs()

            val auditLogsForProjectTasks = getAuditProjectTasks(projectId, auditLogs)

            val auditLogsForProject = auditLogs.filter { it.itemId == projectId }

            val allAuditLogsForProject =
                (auditLogsForProject + auditLogsForProjectTasks).sortedByDescending { it.auditTime }

            allAuditLogsForProject
        }
    }

    private suspend fun getAuditProjectTasks(
        projectId: UUID, auditLogs: List<AuditLog>
    ): List<AuditLog> {
        return executeSafely {
            val tasksResult = taskRepository.getTasks()
            val projectTaskIds = tasksResult.filter { it.projectId == projectId }.map { it.taskId }.toSet()

            val auditLogsForProjectTasks = auditLogs.filter { projectTaskIds.contains(it.itemId) }
            auditLogsForProjectTasks
        }
    }

    override suspend fun getAuditLogs(): List<AuditLog> {
        return executeSafely {
            val lines = fileDataSource.readLinesFromFile()

            lines.mapNotNull { line ->
                try {
                    auditCsvParser.parseCsvLine(line)
                } catch (_: Exception) {
                    null
                }
            }
        }
    }

    companion object {
        const val FILE_NAME: String = "audits.csv"
    }

}