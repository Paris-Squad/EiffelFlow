package org.example.data.repository

import org.example.data.storage.FileDataSource
import org.example.data.storage.parser.AuditCsvParser
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import java.util.*

class AuditRepositoryImpl(
    private val auditCsvParser: AuditCsvParser,
    private val fileDataSource: FileDataSource,
    private val taskRepository: TaskRepository
) : AuditRepository {
    override fun createAuditLog(auditLog: AuditLog): Result<AuditLog> {
        return try {
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
            Result.success(auditLog)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTaskAuditLogById(taskId: UUID): Result<List<AuditLog>> {
        val lines = fileDataSource.readLinesFromFile()
        if (lines.isEmpty()) return Result.success(emptyList())

        val auditLogs = lines.map { line ->
            auditCsvParser.parseCsvLine(line)
        }.filter { it.itemId == taskId }

        return if (auditLogs.isEmpty()) {
            Result.failure(EiffelFlowException.NotFoundException("Audit logs not found for item ID: $taskId"))
        } else {
            Result.success(auditLogs)
        }
    }

    override fun getProjectAuditLogById(projectId: UUID): Result<List<AuditLog>> {
        return try {
            val csvLines = fileDataSource.readLinesFromFile()
            if (csvLines.isEmpty()) return Result.success(emptyList())

            val tasksResult = taskRepository.getTasks()

            val tasksForProject =
                tasksResult.filter { it.projectId == projectId }.map { it.taskId }.toSet()

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
                Result.failure(
                    EiffelFlowException.NotFoundException("No audit logs found for project or related tasks: $projectId")
                )
            } else {
                Result.success(projectAuditLogs)
            }

        } catch (exception: Exception) {
            return Result.failure(exception)
        }
    }

    override fun getAuditLogs(): Result<List<AuditLog>> {
        val lines = fileDataSource.readLinesFromFile()
        if (lines.isEmpty()) return Result.failure(EiffelFlowException.NotFoundException("Audit logs not found"))

        val logs = lines.mapNotNull { line ->
            try {
                auditCsvParser.parseCsvLine(line)
            } catch (e: Exception) {
                null
            }
        }

        return if (logs.isEmpty()) {
            Result.failure(EiffelFlowException.NotFoundException("Audit logs not found"))
        } else {
            Result.success(logs)
        }

    }

    companion object {
        const val FILE_NAME: String = "audits.csv"
    }

}