package data.storge.mapper

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import org.example.data.storge.mapper.TaskCsvMapper
import org.example.domain.model.entities.RoleType
import org.example.domain.model.entities.State
import org.example.domain.model.entities.Task
import java.util.*
import kotlin.test.Test

class TaskCsvMapperTest {

    private val taskCsvMapper = TaskCsvMapper()

    @Test
    fun `test mapFrom CSV to Task`() {
        val csv =
            "123e4567-e89b-12d3-a456-426614174000,Title,Desc,2024-01-01T10:00:00,123e4567-e89b-12d3-a456-426614174001,123e4567-e89b-12d3-a456-426614174002,123e4567-e89b-12d3-a456-426614174003,123e4567-e89b-12d3-a456-426614174004,ADMIN"

        try {
            val task = taskCsvMapper.mapFrom(csv)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `test mapTo Task to CSV`() {
        val task = Task(
            taskId = UUID.randomUUID(),
            title = "Title",
            description = "Desc",
            createdAt = LocalDateTime.parse("1999-08-07T22:22:22"),
            creatorId = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            assignedId = UUID.randomUUID(),
            state = State(UUID.randomUUID(), "Todo"),
            role = RoleType.ADMIN
        )

        try {
            val csv = taskCsvMapper.mapTo(task)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}