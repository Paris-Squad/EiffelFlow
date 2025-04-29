package org.example.data.storge.mapper

import org.example.data.storge.Mapper
import org.example.domain.model.entities.State
import org.example.data.utils.StateCsvColumnIndex
import java.util.UUID

class StateCsvMapper : Mapper<String, State> {

    override fun mapFrom(input: String): State {
        TODO("Not yet implemented")
    }

    override fun mapTo(output: State): String {
        TODO("Not yet implemented")
    }
}