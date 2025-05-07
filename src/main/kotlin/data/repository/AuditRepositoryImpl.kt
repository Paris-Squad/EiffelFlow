package org.example.data.repository

import org.example.data.storage.FileDataSource
import org.example.data.storage.parser.AuditCsvParser
import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import java.util.*

class AuditRepositoryImpl(
    private val auditCsvParser: AuditCsvParser,
    private val fileDataSource: FileDataSource,
    private val taskRepository: TaskRepository
) : AuditRepository {
    override fun createAuditLog(auditLog: AuditLog): AuditLog {
        val line = auditCsvParser.serialize(auditLog)
        fileDataSource.writeLinesToFile(line)
        return auditLog
    }

    override fun getTaskAuditLogById(taskId: UUID): List<AuditLog> {
        return getAuditLogs().filter { it.itemId == taskId }
    }

    override fun getProjectAuditLogById(projectId: UUID): List<AuditLog> {
        val auditLogs = getAuditLogs()

        val auditLogsForProjectTasks = getAuditProjectTasks(projectId, auditLogs)

        val auditLogsForProject = auditLogs.filter { it.itemId == projectId }

        val allAuditLogsForProject =
            (auditLogsForProject + auditLogsForProjectTasks).sortedByDescending { it.auditTime }

        return allAuditLogsForProject

    }

    private fun getAuditProjectTasks(
        projectId: UUID, auditLogs: List<AuditLog>
    ): List<AuditLog> {
        val tasksResult = taskRepository.getTasks()
        val projectTaskIds = tasksResult.filter { it.projectId == projectId }.map { it.taskId }.toSet()

        val auditLogsForProjectTasks = auditLogs.filter { projectTaskIds.contains(it.itemId) }
        return auditLogsForProjectTasks
    }

    override fun getAuditLogs(): List<AuditLog> {
        val lines = fileDataSource.readLinesFromFile()

        return lines.mapNotNull { line ->
            try {
                auditCsvParser.parseCsvLine(line)
            } catch (e: Exception) {
                null
            }
        }
    }

    companion object {
        const val FILE_NAME: String = "audits.csv"
    }

}