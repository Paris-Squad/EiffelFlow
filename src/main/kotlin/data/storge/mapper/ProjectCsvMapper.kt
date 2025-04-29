package org.example.data.storge.mapper

import kotlinx.datetime.LocalDateTime
import org.example.data.storge.Mapper
import org.example.data.utils.ProjectCsvColumnIndex
import org.example.domain.model.entities.Project
import java.util.UUID

class ProjectCsvMapper : Mapper<String, Project> {

    override fun mapFrom(input: String): Project {
        TODO("Not yet implemented")
    }

    override fun mapTo(output: Project): String {
        TODO("Not yet implemented")
    }
}

