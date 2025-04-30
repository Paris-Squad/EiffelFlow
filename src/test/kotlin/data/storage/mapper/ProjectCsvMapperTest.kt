package data.storage.mapper

import com.google.common.truth.Truth.assertThat
import org.example.data.storage.mapper.ProjectCsvMapper
import org.example.data.storage.mapper.StateCsvMapper
import utils.MockProjects
import kotlin.test.Test

class ProjectCsvMapperTest {

    private val stateCsvMapper = StateCsvMapper()
    private val projectCsvMapper = ProjectCsvMapper(stateCsvMapper)

    @Test
    fun `should map CSV line to Project entity correctly`() {

        //Given / When
        val result = projectCsvMapper.mapFrom(MockProjects.CORRECT_CSV_STRING_LINE)

        // Then
        assertThat(result).isEqualTo(MockProjects.CORRECT_PROJECT)

    }

    @Test
    fun `should map CSV line to Project entity line with empty states when state part not start with opening bracket not found`() {

        //Given / When
        val result = projectCsvMapper.mapFrom(MockProjects.CORRECT_CSV_STRING_LINE.replace('[', '{'))

        // Then
        assertThat(result).isEqualTo(MockProjects.CORRECT_PROJECT.copy(states = emptyList()))
    }

    @Test
    fun `should map CSV line to Project entity line with empty states when state part not start with closing bracket not found`() {

        //Given / When
        val result = projectCsvMapper.mapFrom(MockProjects.CORRECT_CSV_STRING_LINE.replace(']', '{'))

        // Then
        assertThat(result).isEqualTo(MockProjects.CORRECT_PROJECT.copy(states = emptyList()))
    }

    @Test
    fun `should map Project entity to CSV line correctly`() {

        //Given / When
        val result = projectCsvMapper.mapTo(MockProjects.CORRECT_PROJECT)

        // Then
        assertThat(result).isEqualTo(MockProjects.CORRECT_CSV_STRING_LINE)
    }

    @Test
    fun `should map Project entity to CSV line with empty states when state part is blank`() {

        //Given / When
        val result = projectCsvMapper.mapTo(MockProjects.CORRECT_PROJECT.copy(states = emptyList()))

        // Then
        assertThat(result).isEqualTo(MockProjects.CORRECT_CSV_STRING_LINE_WITH_EMPTY_STATES)
    }
}
