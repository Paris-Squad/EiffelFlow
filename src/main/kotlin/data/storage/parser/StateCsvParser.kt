package org.example.data.storage.parser

import org.example.data.storage.CsvParser
import org.example.domain.model.TaskState
import org.example.data.utils.StateCsvColumnIndex
import java.util.UUID

class StateCsvParser : CsvParser<TaskState> {

    override fun parseCsvLine(csvLine: String): TaskState {
        val parts = csvLine.split(",")

        return TaskState(
            stateId = UUID.fromString(parts[StateCsvColumnIndex.STATE_ID].trim()),
            name = parts[StateCsvColumnIndex.STATE_NAME]
        )
    }

    override fun serialize(item: TaskState): String {
        return listOf(
            item.stateId.toString(),
            item.name
        ).joinToString(",")
    }
}