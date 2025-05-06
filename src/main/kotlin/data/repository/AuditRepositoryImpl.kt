package org.example.data.repository

import org.example.data.storage.FileDataSource
import org.example.data.storage.parser.AuditCsvParser
import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import java.util.UUID

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
        val lines = fileDataSource.readLinesFromFile()

        return lines.map { line ->
            auditCsvParser.parseCsvLine(line)
        }.filter { it.itemId == taskId }
    }

    override fun getProjectAuditLogById(projectId: UUID): List<AuditLog> {
        val csvLines = fileDataSource.readLinesFromFile()

        val tasksResult = taskRepository.getTasks()

        val tasksForProject = tasksResult.filter { it.projectId == projectId }.map { it.taskId }.toSet()

        val parsedAuditLogs = csvLines.mapNotNull { line ->
            try {
                auditCsvParser.parseCsvLine(line)
            } catch (e: Exception) {
                null
            }
        }

         return parsedAuditLogs.filter { log ->
            tasksForProject.contains(log.itemId)
        }

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