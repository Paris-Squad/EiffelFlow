package org.example.data.storge.mapper

import org.example.data.storge.CsvMapper
import org.example.domain.model.Project

class ProjectCsvMapper : CsvMapper<Project> {
    override fun fromCsv(columns: List<String>): Project {
        TODO()
    }

    override fun toCsv(entity: Project): List<String> {
        TODO()
    }
}
