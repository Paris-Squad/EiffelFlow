package data.local.csvparser

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import org.example.data.local.parser.StateCsvParser
import org.example.data.local.parser.TaskCsvParser
import org.example.domain.model.RoleType
import org.example.domain.model.TaskState
import org.example.domain.model.Task
import java.util.*
import kotlin.test.Test

class TaskCsvParserTest {

    private val stateCsvParser = StateCsvParser()
    private val taskCsvParser = TaskCsvParser(stateCsvParser)

    @Test
    fun `should map CSV line to Task entity correctly`() {
        // Given / When
        val result = taskCsvParser.parseCsvLine(CSV_STRING_LINE)

        // Then
        assertThat(result).isEqualTo(TASK)
    }

    @Test
    fun `should map Task entity to CSV line correctly`() {
        // Given / When
        val result = taskCsvParser.serialize(TASK)

        // Then
        assertThat(result).isEqualTo(CSV_STRING_LINE)
    }

    companion object {
        private val STATE = TaskState(
            stateId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5749"),
            name = "In Progress"
        )

        private val TASK = Task(
            taskId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748"),
            title = "Title",
            description = "Desc",
            createdAt = LocalDateTime.parse("1999-08-07T22:22:22"),
            creatorId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5740"),
            projectId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5741"),
            assignedId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5742"),
            state = STATE,
            role = RoleType.ADMIN
        )

        private const val CSV_STRING_LINE =
            "02ad4499-5d4c-4450-8fd1-8294f1bb5748,Title,Desc,1999-08-07T22:22:22,02ad4499-5d4c-4450-8fd1-8294f1bb5740,02ad4499-5d4c-4450-8fd1-8294f1bb5741,02ad4499-5d4c-4450-8fd1-8294f1bb5742,02ad4499-5d4c-4450-8fd1-8294f1bb5749,In Progress,ADMIN"
    }
}