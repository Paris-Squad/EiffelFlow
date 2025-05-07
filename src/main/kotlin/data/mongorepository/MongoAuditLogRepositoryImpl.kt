package data.mongorepository


import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import java.util.UUID

class MongoAuditLogRepositoryImpl: AuditRepository {

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
        const val COLLECTION_NAME = "audit_logs"
    }
}