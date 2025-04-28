package org.example.data.storge.mapper


import org.example.data.storge.Mapper
import org.example.domain.model.entities.Task

class TaskCsvMapper : Mapper<List<String>, Task> {
    override fun mapFrom(input: List<String>): Task {
        TODO("Not yet implemented")
    }

    override fun mapTo(output: Task): List<String> {
        TODO("Not yet implemented")
    }

}