package org.example.data.local.parser

import org.example.data.local.csvparser.CsvParser
import org.example.domain.model.Task
import kotlinx.datetime.LocalDateTime
import org.example.data.local.utils.TaskCsvColumnIndex
import org.example.domain.model.RoleType

import java.util.UUID

class TaskCsvParser(
    private val StateCsvParser: StateCsvParser
) : CsvParser<Task> {

    override fun parseCsvLine(csvLine: String): Task {
        val parts = csvLine.split(",")

        val stateString = listOf(
            parts[TaskCsvColumnIndex.STATE_ID],
            parts[TaskCsvColumnIndex.STATE_NAME]
        ).joinToString(",")

        return Task(
            taskId = UUID.fromString(parts[TaskCsvColumnIndex.TASK_ID]),
            title = parts[TaskCsvColumnIndex.TITLE],
            description = parts[TaskCsvColumnIndex.DESCRIPTION],
            createdAt = LocalDateTime.parse(parts[TaskCsvColumnIndex.CREATED_AT]),
            creatorId = UUID.fromString(parts[TaskCsvColumnIndex.CREATOR_ID]),
            projectId = UUID.fromString(parts[TaskCsvColumnIndex.PROJECT_ID]),
            assignedId = UUID.fromString(parts[TaskCsvColumnIndex.ASSIGNED_ID]),
            state = StateCsvParser.parseCsvLine(stateString),
            role = RoleType.valueOf(parts[TaskCsvColumnIndex.ROLE])
        )
    }

    override fun serialize(item: Task): String {
        val stateString = StateCsvParser.serialize(item.state).split(",") // returns "stateId,stateName"

        return listOf(
            item.taskId.toString(),
            item.title,
            item.description,
            item.createdAt.toString(),
            item.creatorId.toString(),
            item.projectId.toString(),
            item.assignedId.toString(),
            stateString[0],
            stateString[1],
            item.role.name
        ).joinToString(",")
    }
}