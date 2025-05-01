package org.example.data.storage.mapper

import kotlinx.datetime.LocalDateTime
import org.example.data.storage.Mapper
import org.example.data.utils.ProjectCsvColumnIndex
import org.example.domain.model.entities.Project
import org.example.domain.model.entities.State
import java.util.UUID

class ProjectCsvMapper(
    private val stateCsvMapper: StateCsvMapper
) : Mapper<String, Project> {

    override fun mapFrom(input: String): Project {
        val parts = input.split(",", limit = 6)

        return Project(
            projectId = UUID.fromString(parts[ProjectCsvColumnIndex.PROJECT_ID].trim()),
            projectName = parts[ProjectCsvColumnIndex.PROJECT_NAME],
            projectDescription = parts[ProjectCsvColumnIndex.PROJECT_DESCRIPTION],
            createdAt = LocalDateTime.parse(parts[ProjectCsvColumnIndex.CREATED_AT].trim()),
            adminId = UUID.fromString(parts[ProjectCsvColumnIndex.ADMIN_ID].trim()),
            states = mapToStates(parts[ProjectCsvColumnIndex.STATE])
        )
    }

    private fun mapToStates(input: String): List<State> {
        if (
            input.isBlank() ||
            input.startsWith("[").not() ||
            input.endsWith("]").not()
        ) {
            return emptyList()
        }
        val content = input.removeSurrounding("[", "]").split(";")
        return content.map { stateCsvMapper.mapFrom(it) }
    }

    override fun mapTo(output: Project): String {
        return listOf(
            output.projectId.toString(),
            output.projectName,
            output.projectDescription,
            output.createdAt.toString(),
            output.adminId.toString(),
            mapFromStates(output.states)
        ).joinToString(",")
    }

    private fun mapFromStates(states: List<State>): String {
        val statesString = states
            .joinToString(";") { stateCsvMapper.mapTo(it) }
        return if (statesString.isNotBlank()) {
            "[$statesString]"
        } else {
            statesString
        }
    }
}


