package org.example.data.respoitory

import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import org.example.data.storge.DataSource
import java.util.*

class AuditRepositoryImpl(
    private val dataSource: DataSource<AuditLog>
) : AuditRepository {
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