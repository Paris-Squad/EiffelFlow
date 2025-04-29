package data.storage.mapper

import com.google.common.truth.Truth.assertThat
import org.example.data.storage.mapper.StateCsvMapper
import org.example.domain.model.entities.State
import java.util.*
import kotlin.test.Test

class StateCsvMapperTest {

    private val stateCsvMapper = StateCsvMapper()

    @Test
    fun `should map CSV line to State entity correctly`() {

        //Given / When
        val result = stateCsvMapper.mapFrom(CSV_STRING_LINE)

        // Then
        assertThat(result).isEqualTo(STATE)

    }

    @Test
    fun `should map State entity to CSV line correctly`() {

        //Given / When
        val result = stateCsvMapper.mapTo(STATE)

        // Then
        assertThat(result).isEqualTo(CSV_STRING_LINE)
    }

    companion object {
        private val STATE = State(
            stateId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748"),
            name = "In Progress"
        )

        private const val CSV_STRING_LINE = "02ad4499-5d4c-4450-8fd1-8294f1bb5748,In Progress"
    }
}
