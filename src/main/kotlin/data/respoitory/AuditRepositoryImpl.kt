package org.example.data.respoitory

import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import java.util.*

class AuditRepositoryImpl : AuditRepository {
    override fun logChange(auditLog: AuditLog) {
        TODO("Not yet implemented")
    }

    override fun getLogByItemId(itemId: UUID): AuditLog {
        TODO("Not yet implemented")
    }

    override fun getAllLogs(): List<AuditLog> {
        TODO("Not yet implemented")
    }
}