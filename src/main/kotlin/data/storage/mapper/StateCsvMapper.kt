package org.example.data.storage.mapper

import org.example.data.storage.Mapper
import org.example.domain.model.TaskState
import org.example.data.utils.StateCsvColumnIndex
import java.util.UUID

class StateCsvMapper : Mapper<String, TaskState> {

    override fun mapFrom(input: String): TaskState {
        val parts = input.split(",")

        return TaskState(
            stateId = UUID.fromString(parts[StateCsvColumnIndex.STATE_ID].trim()),
            name = parts[StateCsvColumnIndex.STATE_NAME]
        )
    }

    override fun mapTo(output: TaskState): String {
        return listOf(
            output.stateId.toString(),
            output.name
        ).joinToString(",")
    }
}