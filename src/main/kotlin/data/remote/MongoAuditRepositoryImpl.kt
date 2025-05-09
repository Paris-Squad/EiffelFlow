package data.mongorepository

import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.MongoCollections
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import java.util.UUID

class MongoAuditRepositoryImpl(
    database: MongoDatabase,
    taskRepositoryProvider: Lazy<TaskRepository>
) : AuditRepository {

    private val taskRepository: TaskRepository by taskRepositoryProvider
    private val auditLogsCollection = database.getCollection<AuditLog>(collectionName = MongoCollections.AUDIT_LOGS)

    override suspend fun createAuditLog(auditLog: AuditLog): AuditLog {
        try {
            val existingLog = auditLogsCollection.find(eq("auditId", auditLog.auditId)).firstOrNull()
            if (existingLog != null) {
                throw EiffelFlowException.IOException("AuditLog with auditId ${auditLog.auditId} already exists")
            }
            auditLogsCollection.insertOne(auditLog)
            return auditLog
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't create AuditLog because ${exception.message}")
        }
    }

    override suspend fun getTaskAuditLogById(taskId: UUID): List<AuditLog> {
        try {
            return findAuditLogsByItemId(taskId).toList()
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Unexpected error fetching audit logs ${exception.message}")
        }
    }

    override suspend fun getProjectAuditLogById(projectId: UUID): List<AuditLog> {
        try {
            val projectLogs = findAuditLogsByItemId(projectId).toList()
            val auditLogsForProjectTasks = getAuditProjectTasks(projectId, getAuditLogs())

            return (projectLogs + auditLogsForProjectTasks).sortedByDescending { it.auditTime }
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Unexpected error fetching audit logs ${exception.message}")
        }
    }

    private fun findAuditLogsByItemId(itemId: UUID): FindFlow<AuditLog> {
        return auditLogsCollection
            .find(eq("itemId", itemId))
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
        try {
            return auditLogsCollection.find().toList()
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't get AuditLogs because ${exception.message}")
        }
    }
}