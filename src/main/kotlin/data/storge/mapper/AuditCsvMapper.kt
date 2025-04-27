package org.example.data.storge.mapper

import org.example.data.storge.CsvMapper
import org.example.domain.model.AuditLog
import org.example.domain.model.User

class AuditCsvMapper : CsvMapper<AuditLog> {
    override fun fromCsv(columns: List<String>): AuditLog {
        TODO()
    }

    override fun toCsv(entity: AuditLog): List<String> {
        TODO()
    }
}
