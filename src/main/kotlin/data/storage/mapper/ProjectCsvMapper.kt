package org.example.data.storage.mapper

import kotlinx.datetime.LocalDateTime
import org.example.data.storage.Mapper
import org.example.data.utils.ProjectCsvColumnIndex
import org.example.domain.model.entities.Project
import org.example.domain.model.entities.State
import java.util.UUID

class ProjectCsvMapper : Mapper<String, Project> {

    override fun mapFrom(input: String): Project {
        val parts = input.split(",", limit = 6)
        val statesPart = parts.getOrNull(5)?.removeSurrounding("[", "]") ?: ""

        return Project(
            projectId = UUID.fromString(parts[ProjectCsvColumnIndex.PROJECT_ID]),
            projectName = parts[ProjectCsvColumnIndex.PROJECT_NAME],
            projectDescription = parts[ProjectCsvColumnIndex.PROJECT_DESCRIPTION],
            createdAt = LocalDateTime.parse(parts[ProjectCsvColumnIndex.CREATED_AT]),
            adminId = UUID.fromString(parts[ProjectCsvColumnIndex.ADMIN_ID]),
            states = mappingStateList(statesPart))
    }

    override fun mapTo(output: Project): String {
        val statesString = output.states.joinToString(";") { "${it.stateId}, ${it.name}" }

        return listOf(
            output.projectId.toString(),
            output.projectName,
            output.projectDescription,
            output.createdAt.toString(),
            output.adminId.toString(),
            "[$statesString]"
        ).joinToString(",")
    }

    private fun mappingStateList(statesPart: String)=
        statesPart.split(";").mapNotNull { stateStr ->
            stateStr.trim().takeIf { it.isNotEmpty() }?.let {
                val stateParts = it.split(",")
                State(
                    stateId = UUID.fromString(stateParts[0]),
                    name = stateParts[1].trim()
                )
            }
        }

}

