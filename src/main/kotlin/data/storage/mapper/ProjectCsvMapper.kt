package org.example.data.storage.mapper

import kotlinx.datetime.LocalDateTime
import org.example.data.storage.Mapper
import org.example.data.utils.ProjectCsvColumnIndex
import org.example.domain.model.entities.Project
import java.util.UUID

class ProjectCsvMapper : Mapper<String, Project> {

    override fun mapFrom(input: String): Project {
        val parts = input.split(",")

        return Project(
            projectId = UUID.fromString(parts[ProjectCsvColumnIndex.PROJECT_ID]),
            projectName = parts[ProjectCsvColumnIndex.PROJECT_NAME],
            projectDescription = parts[ProjectCsvColumnIndex.PROJECT_DESCRIPTION],
            createdAt = LocalDateTime.parse(parts[ProjectCsvColumnIndex.CREATED_AT]),
            adminId = UUID.fromString(parts[ProjectCsvColumnIndex.ADMIN_ID]),
            states = emptyList() // load separately
        )
    }

    override fun mapTo(output: Project): String {
        return listOf(
            output.projectId.toString(),
            output.projectName,
            output.projectDescription,
            output.createdAt.toString(),
            output.adminId.toString()
        ).joinToString(",")
    }
}

