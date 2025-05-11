package data.remote.repository

import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.example.data.BaseRepository
import org.example.data.remote.MongoCollections
import org.example.data.remote.dto.MongoAuditLogDto
import org.example.data.remote.mapper.AuditLogMapper
import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import java.util.UUID

class AuditRepositoryImpl(
    database: MongoDatabase,
    taskRepositoryProvider: Lazy<TaskRepository>,
    private val auditLogMapper: AuditLogMapper
) : BaseRepository(), AuditRepository {

    private val taskRepository: TaskRepository by taskRepositoryProvider
    private val auditLogsCollection =
        database.getCollection<MongoAuditLogDto>(collectionName = MongoCollections.AUDIT_LOGS)

    override suspend fun createAuditLog(auditLog: AuditLog): AuditLog {
        return wrapInTryCatch {
            val auditLogDto = auditLogMapper.toDto(auditLog)
            auditLogsCollection.insertOne(auditLogDto)
            auditLog
        }
    }

    override suspend fun getTaskAuditLogById(taskId: UUID): List<AuditLog> {
        return wrapInTryCatch {
            findAuditLogsByItemId(taskId).toList().sortedByDescending { it.auditTime }
        }
    }

    override suspend fun getProjectAuditLogById(projectId: UUID): List<AuditLog> {
        return wrapInTryCatch {
            val projectLogs = findAuditLogsByItemId(projectId).toList()
            val auditLogsForProjectTasks = getAuditProjectTasks(projectId, getAuditLogs())
            (projectLogs + auditLogsForProjectTasks).sortedByDescending { it.auditTime }
        }
    }

    private fun findAuditLogsByItemId(itemId: UUID): Flow<AuditLog> {
        val auditLogsDto = auditLogsCollection
            .find(eq(MongoAuditLogDto::itemId.name, itemId.toString()))
        return auditLogsDto.map { auditLogMapper.fromDto(it) }
    }

    private suspend fun getAuditProjectTasks(
        projectId: UUID,
        auditLogs: List<AuditLog>
    ): List<AuditLog> {
        val tasksResult = taskRepository.getTasks()
        val projectTaskIds = tasksResult.filter { it.projectId == projectId }.map { it.taskId }.toSet()

        val auditLogsForProjectTasks = auditLogs.filter { projectTaskIds.contains(it.itemId) }
        return auditLogsForProjectTasks
    }

    override suspend fun getAuditLogs(): List<AuditLog> {
        return wrapInTryCatch {
            val auditLogsDto = auditLogsCollection.find().toList()
            auditLogsDto.map { auditLogMapper.fromDto(it) }
        }
    }
}