package org.example.data.storge.mapper

import org.example.data.storge.Mapper
import org.example.domain.model.entities.AuditLog

class AuditCsvMapper : Mapper<List<String>, AuditLog> {
    override fun mapFrom(input: List<String>): AuditLog {
        TODO("Not yet implemented")
    }

    override fun mapTo(output: AuditLog): List<String> {
        val stringBuilder = mutableListOf<String>()
        stringBuilder.add(output.auditId.toString())
        stringBuilder.add(output.itemId.toString())
        stringBuilder.add(output.itemName)
        stringBuilder.add(output.userId.toString())
        stringBuilder.add(output.userName)
        stringBuilder.add(output.actionType.toString())
        stringBuilder.add(output.auditTime.toString())
        stringBuilder.add(output.changedField.toString())
        stringBuilder.add(output.oldValue.toString())
        stringBuilder.add(output.newValue.toString())
        return stringBuilder
    }
}