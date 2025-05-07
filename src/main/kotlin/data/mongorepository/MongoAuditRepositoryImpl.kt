package data.mongorepository

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import java.util.UUID

class MongoAuditRepositoryImpl(
    private val database: MongoDatabase,
) : AuditRepository {

    private val auditLogsCollection = database.getCollection<AuditLog>(collectionName = COLLECTION_NAME)


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

    companion object {
        private const val COLLECTION_NAME = "audit_logs"
    }
}