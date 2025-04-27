package org.example.data.storge.mapper

import org.example.data.storge.CsvMapper
import org.example.domain.model.Task

class TaskCsvMapper : CsvMapper<Task> {
    override fun fromCsv(columns: List<String>): Task {
        TODO()
    }

    override fun toCsv(entity: Task): List<String> {
        TODO()
    }
}
