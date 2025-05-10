package org.example.data.local.parser

import org.example.data.local.csvparser.CsvParser
import org.example.domain.model.TaskState
import org.example.data.local.utils.StateCsvColumnIndex
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