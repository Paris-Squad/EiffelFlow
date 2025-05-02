package data.storage.parser

import com.google.common.truth.Truth.assertThat
import org.example.data.storage.parser.ProjectCsvParser
import org.example.data.storage.parser.StateCsvParser
import utils.ProjectsMock
import kotlin.test.Test

class ProjectCsvParserTest {

    private val StateCsvParser = StateCsvParser()
    private val ProjectCsvParser = ProjectCsvParser(StateCsvParser)

    @Test
    fun `should map CSV line to Project entity correctly`() {

        //Given / When
        val result = ProjectCsvParser.parseCsvLine(ProjectsMock.CORRECT_CSV_STRING_LINE)

        // Then
        assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)

    }

    @Test
    fun `should map CSV line to Project entity line with empty states when state part not start with opening bracket not found`() {

        //Given / When
        val result = ProjectCsvParser.parseCsvLine(ProjectsMock.CORRECT_CSV_STRING_LINE.replace('[', '{'))

        // Then
        assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT.copy(taskStates = emptyList()))
    }

    @Test
    fun `should map CSV line to Project entity line with empty states when state part not start with closing bracket not found`() {

        //Given / When
        val result = ProjectCsvParser.parseCsvLine(ProjectsMock.CORRECT_CSV_STRING_LINE.replace(']', '{'))

        // Then
        assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT.copy(taskStates = emptyList()))
    }

    @Test
    fun `should map CSV line to Project entity line with empty states when state part is blank`() {

        //Given / When
        val result = ProjectCsvParser.parseCsvLine(ProjectsMock.CORRECT_CSV_STRING_LINE_WITH_EMPTY_STATES)

        // Then
        assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT.copy(taskStates = emptyList()))
    }

    @Test
    fun `should map Project entity to CSV line correctly`() {

        //Given / When
        val result = ProjectCsvParser.serialize(ProjectsMock.CORRECT_PROJECT)

        // Then
        assertThat(result).isEqualTo(ProjectsMock.CORRECT_CSV_STRING_LINE)
    }

    @Test
    fun `should map Project entity to CSV line with empty states when state part is blank`() {

        //Given / When
        val result = ProjectCsvParser.serialize(ProjectsMock.CORRECT_PROJECT.copy(taskStates = emptyList()))

        // Then
        assertThat(result).isEqualTo(ProjectsMock.CORRECT_CSV_STRING_LINE_WITH_EMPTY_STATES)
    }
}
