package org.example.data.local.parser

import kotlinx.datetime.LocalDateTime
import org.example.data.local.csvparser.CsvParser
import org.example.data.local.utils.ProjectCsvColumnIndex
import org.example.domain.model.Project
import org.example.domain.model.TaskState
import java.util.UUID

class ProjectCsvParser(
    private val StateCsvParser: StateCsvParser
) : CsvParser<Project> {

    override fun parseCsvLine(csvLine: String): Project {
        val parts = csvLine.split(",", limit = 6)

        return Project(
            projectId = UUID.fromString(parts[ProjectCsvColumnIndex.PROJECT_ID].trim()),
            projectName = parts[ProjectCsvColumnIndex.PROJECT_NAME],
            projectDescription = parts[ProjectCsvColumnIndex.PROJECT_DESCRIPTION],
            createdAt = LocalDateTime.parse(parts[ProjectCsvColumnIndex.CREATED_AT].trim()),
            adminId = UUID.fromString(parts[ProjectCsvColumnIndex.ADMIN_ID].trim()),
            taskStates = mapToStates(parts[ProjectCsvColumnIndex.STATE])
        )
    }

    private fun mapToStates(input: String): List<TaskState> {
        if (
            input.isBlank() ||
            input.startsWith("[").not() ||
            input.endsWith("]").not()
        ) {
            return emptyList()
        }
        val content = input.removeSurrounding("[", "]").split(";")
        return content.map { StateCsvParser.parseCsvLine(it) }
    }

    override fun serialize(item: Project): String {
        return listOf(
            item.projectId.toString(),
            item.projectName,
            item.projectDescription,
            item.createdAt.toString(),
            item.adminId.toString(),
            mapFromStates(item.taskStates)
        ).joinToString(",")
    }

    private fun mapFromStates(states: List<TaskState>): String {
        val statesString = states
            .joinToString(";") { StateCsvParser.serialize(it) }
        return if (statesString.isNotBlank()) {
            "[$statesString]"
        } else {
            statesString
        }
    }
}

