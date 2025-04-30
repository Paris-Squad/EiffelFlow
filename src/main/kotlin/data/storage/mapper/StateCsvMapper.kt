package org.example.data.storage.mapper

import org.example.data.storage.Mapper
import org.example.domain.model.entities.State
import org.example.data.utils.StateCsvColumnIndex
import java.util.UUID

class StateCsvMapper : Mapper<String, State> {

    override fun mapFrom(input: String): State {
        val parts = input.split(",")

        return State(
            stateId = UUID.fromString(parts[StateCsvColumnIndex.STATE_ID]),
            name = parts[StateCsvColumnIndex.STATE_NAME]
        )
    }

    override fun mapTo(output: State): String {
        return listOf(
            output.stateId.toString(),
            output.name
        ).joinToString(",")
    }
}