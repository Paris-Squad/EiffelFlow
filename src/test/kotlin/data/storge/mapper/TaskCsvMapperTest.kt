package data.storge.mapper

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.example.data.storge.mapper.StateCsvMapper
import org.example.data.storge.mapper.TaskCsvMapper
import org.example.domain.model.entities.RoleType
import org.example.domain.model.entities.State
import org.example.domain.model.entities.Task
import java.util.*
import kotlin.test.Test

class TaskCsvMapperTest {

    private val stateCsvMapper = StateCsvMapper()
    private val taskCsvMapper = TaskCsvMapper(stateCsvMapper)

    @Test
    fun `should map CSV line to Task entity correctly`() {
        // Given / When
        val result = taskCsvMapper.mapFrom(CSV_STRING_LINE)

        // Then
        assertThat(result).isEqualTo(TASK)
    }

    @Test
    fun `should map Task entity to CSV line correctly`() {
        // Given / When
        val result = taskCsvMapper.mapTo(TASK)

        // Then
        assertThat(result).isEqualTo(CSV_STRING_LINE)
    }

    companion object {
        private val STATE = State(
            stateId = UUID.fromString("123e4567-e89b-12d3-a456-426614174004"),
            name = "In Progress"
        )

        private val TASK = Task(
            taskId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
            title = "Title",
            description = "Desc",
            createdAt = LocalDateTime.parse("1999-08-07T22:22:22"),
            creatorId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001"),
            projectId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002"),
            assignedId = UUID.fromString("123e4567-e89b-12d3-a456-426614174003"),
            state = STATE,
            role = RoleType.ADMIN
        )

        private const val CSV_STRING_LINE =
            "123e4567-e89b-12d3-a456-426614174000,Title,Desc,1999-08-07T22:22:22,123e4567-e89b-12d3-a456-426614174001,123e4567-e89b-12d3-a456-426614174002,123e4567-e89b-12d3-a456-426614174003,123e4567-e89b-12d3-a456-426614174004,In Progress,ADMIN"
    }
}