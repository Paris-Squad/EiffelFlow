package org.example.data.storge.mapper

import org.example.data.storge.Mapper
import org.example.domain.model.entities.State

class StateCsvMapper : Mapper<List<String>, State> {
    override fun mapFrom(input: List<String>): State {
        TODO("Not yet implemented")
    }

    override fun mapTo(output: State): List<String> {
        TODO("Not yet implemented")
    }
}