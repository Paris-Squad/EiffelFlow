package org.example.data.repository

import org.example.data.storage.FileDataSource
import org.example.data.storage.mapper.AuditCsvMapper
import org.example.data.storage.task.TaskDataSource
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import java.util.*

class AuditRepositoryImpl(
    private val auditMapper: AuditCsvMapper,
    private val csvManager: FileDataSource,
    private val taskRepository: TaskDataSource,
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

            csvManager.writeLinesToFile(line)
            Result.success(auditLog)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTaskAuditLogById(taskId: UUID): Result<List<AuditLog>> {
        val lines = csvManager.readLinesFromFile()
        if (lines.isEmpty()) return Result.success(emptyList())

        val auditLogs = lines.map { line ->
            auditMapper.mapFrom(line)
        }.filter { it.itemId == taskId }

        return if (auditLogs.isEmpty()) {
            Result.failure(EiffelFlowException.NotFoundException("Audit logs not found for item ID: $taskId"))
        } else {
            Result.success(auditLogs)
        }
    }

    override fun getProjectAuditLogById(projectId: UUID): Result<List<AuditLog>> {
        return try {
            val lines = csvManager.readLinesFromFile()
            if (lines.isEmpty()) return Result.success(emptyList())

            val allTasksResult = taskRepository.getTasks()
            if (allTasksResult.isFailure) return Result.failure(allTasksResult.exceptionOrNull()!!)

            val tasks = allTasksResult.getOrThrow()
            val taskIdsInProject = tasks.filter { it.projectId == projectId }.map { it.taskId }.toSet()

            val failedLines = mutableListOf<String>()

            val auditLogs = lines.mapNotNull { line ->
                try {
                    auditMapper.mapFrom(line)
                } catch (e: Exception) {
                    failedLines.add(line)
                    null
                }
            }

            val relevantLogs = auditLogs.filter { log ->
                log.itemId == projectId || log.itemId in taskIdsInProject
            }

            return if (relevantLogs.isEmpty()) {
                Result.failure(EiffelFlowException.NotFoundException("No audit logs for project or its tasks"))
            } else {
                Result.success(relevantLogs)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAuditLogs(): Result<List<AuditLog>> {
        val lines = csvManager.readLinesFromFile()
        if (lines.isEmpty()) return Result.failure(EiffelFlowException.NotFoundException("Audit logs not found"))

        val logs = lines.mapNotNull { line ->
            try {
                auditMapper.mapFrom(line)
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