package org.example.data.storge.mapper

import kotlinx.datetime.LocalDateTime
import org.example.data.storge.Mapper
import org.example.data.utils.AuditCsvColumnIndex
import org.example.domain.model.entities.AuditAction
import org.example.domain.model.entities.AuditLog
import java.util.UUID

class AuditCsvMapper : Mapper<String, AuditLog> {
    override fun mapFrom(input: String): AuditLog {
        TODO("Not yet implemented")
    }

    override fun mapTo(output: AuditLog): String {
        TODO("Not yet implemented")
    }
}