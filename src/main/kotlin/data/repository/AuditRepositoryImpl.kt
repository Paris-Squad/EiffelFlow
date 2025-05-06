package org.example.data.repository

import org.example.data.storage.FileDataSource
import org.example.data.storage.parser.AuditCsvParser
import org.example.domain.exception.EiffelFlowException
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
        val line = listOf(
            auditLog.auditId.toString(),
            auditLog.itemId.toString(),
            auditLog.itemName,
            auditLog.userId.toString(),
            auditLog.editorName,
            auditLog.actionType.name,
            auditLog.auditTime.toString(),
            auditLog.changedField,
            auditLog.oldValue,
            auditLog.newValue
        ).joinToString(",")
        fileDataSource.writeLinesToFile(line)
        return auditLog
    }

    override fun getTaskAuditLogById(taskId: UUID): List<AuditLog> {
        val lines = fileDataSource.readLinesFromFile()
        if (lines.isEmpty()) return emptyList()

        val auditLogs = lines.map { line ->
            auditCsvParser.parseCsvLine(line)
        }.filter { it.itemId == taskId }

        if (auditLogs.isEmpty()) {
            throw EiffelFlowException.NotFoundException("Audit logs not found for item ID: $taskId")
        } else {
            return auditLogs
        }
    }

    override fun getProjectAuditLogById(projectId: UUID): List<AuditLog> {
        val csvLines = fileDataSource.readLinesFromFile()
        if (csvLines.isEmpty()) return emptyList()

        val tasksResult = taskRepository.getTasks()

        val tasksForProject = tasksResult.filter { it.projectId == projectId }.map { it.taskId }.toSet()

            val parsedAuditLogs = csvLines.mapNotNull { line ->
                try {
                    auditCsvParser.parseCsvLine(line)
                } catch (e: Exception) {
                    null
                }
            }

        val projectAuditLogs = parsedAuditLogs.filter { log ->
            tasksForProject.contains(log.itemId)
        }

        if (projectAuditLogs.isEmpty()) {
            throw EiffelFlowException.NotFoundException("No audit logs found for project or related tasks: $projectId")
        } else return projectAuditLogs
    }

    override fun getAuditLogs(): List<AuditLog> {
        val lines = fileDataSource.readLinesFromFile()
        if (lines.isEmpty()) throw EiffelFlowException.NotFoundException("Audit logs not found")

        val logs = lines.mapNotNull { line ->
            try {
                auditCsvParser.parseCsvLine(line)
            } catch (e: Exception) {
                null
            }
        }

         if (logs.isEmpty()) {
            throw EiffelFlowException.NotFoundException("Audit logs not found")
        } else {
            return logs
        }
    }

    companion object {
        const val FILE_NAME: String = "audits.csv"
    }

}