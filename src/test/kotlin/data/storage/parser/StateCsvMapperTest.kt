package data.storage.parser

import com.google.common.truth.Truth.assertThat
import org.example.data.storage.parser.StateCsvParser
import org.example.domain.model.TaskState
import java.util.*
import kotlin.test.Test

class StateCsvParserTest {

    private val StateCsvParser = StateCsvParser()

    @Test
    fun `should map CSV line to State entity correctly`() {

        //Given / When
        val result = StateCsvParser.parseCsvLine(CSV_STRING_LINE)

        // Then
        assertThat(result).isEqualTo(STATE)

    }

    @Test
    fun `should map State entity to CSV line correctly`() {

        //Given / When
        val result = StateCsvParser.serialize(STATE)

        // Then
        assertThat(result).isEqualTo(CSV_STRING_LINE)
    }

    companion object {
        private val STATE = TaskState(
            stateId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748"),
            name = "In Progress"
        )

        private const val CSV_STRING_LINE = "02ad4499-5d4c-4450-8fd1-8294f1bb5748,In Progress"
    }
}
