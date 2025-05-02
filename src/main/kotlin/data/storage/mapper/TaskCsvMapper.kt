package org.example.data.storage.mapper

import org.example.data.storage.Mapper
import org.example.domain.model.Task
import kotlinx.datetime.LocalDateTime
import org.example.data.utils.TaskCsvColumnIndex
import org.example.domain.model.RoleType

import java.util.UUID

class TaskCsvMapper(
    private val stateCsvMapper: StateCsvMapper
) : Mapper<String, Task> {

    override fun mapFrom(input: String): Task {
        val parts = input.split(",")

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
            state = stateCsvMapper.mapFrom(stateString),
            role = RoleType.valueOf(parts[TaskCsvColumnIndex.ROLE])
        )
    }

    override fun mapTo(output: Task): String {
        val stateString = stateCsvMapper.mapTo(output.state).split(",") // returns "stateId,stateName"

        return listOf(
            output.taskId.toString(),
            output.title,
            output.description,
            output.createdAt.toString(),
            output.creatorId.toString(),
            output.projectId.toString(),
            output.assignedId.toString(),
            stateString[0],
            stateString[1],
            output.role.name
        ).joinToString(",")
    }
}