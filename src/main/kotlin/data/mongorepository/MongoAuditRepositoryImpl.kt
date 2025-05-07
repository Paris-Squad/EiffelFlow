package data.mongorepository

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.example.data.MongoCollections
import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import java.util.UUID

class MongoAuditRepositoryImpl(
    private val database: MongoDatabase,
    private val taskRepository: TaskRepository
) : AuditRepository {

    private val auditLogsCollection = database.getCollection<AuditLog>(collectionName = MongoCollections.AUDIT_LOGS)


    override suspend fun createAuditLog(auditLog: AuditLog): AuditLog {
        TODO("Not yet implemented")
    }

    override suspend fun getTaskAuditLogById(taskId: UUID): List<AuditLog> {
        TODO("Not yet implemented")
    }

    override suspend fun getProjectAuditLogById(projectId: UUID): List<AuditLog> {
        TODO("Not yet implemented")
    }

    override suspend fun getAuditLogs(): List<AuditLog> {
        TODO("Not yet implemented")
    }
}