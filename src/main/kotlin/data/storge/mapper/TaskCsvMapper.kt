package org.example.data.storge.mapper

import org.example.data.storge.Mapper
import org.example.domain.model.entities.Task
import kotlinx.datetime.LocalDateTime
import org.example.data.utils.TaskCsvColumnIndex
import org.example.domain.model.entities.RoleType

import java.util.UUID

class TaskCsvMapper(
    private val stateCsvMapper: StateCsvMapper
) : Mapper<String, Task> {

    override fun mapFrom(input: String): Task {
        TODO("Not yet implemented")
    }

    override fun mapTo(output: Task): String {
        TODO("Not yet implemented")
    }
}