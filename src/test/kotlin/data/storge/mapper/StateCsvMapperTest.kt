package data.storge.mapper

import com.google.common.truth.Truth.assertThat
import org.example.data.storge.mapper.StateCsvMapper
import org.example.domain.model.entities.State
import java.util.*
import kotlin.test.Test

class StateCsvMapperTest {

    private val stateCsvMapper = StateCsvMapper()

    @Test
    fun `should map CSV line to Project entity correctly`() {

        //Given / When
        val result = stateCsvMapper.mapFrom(CSV_STRING_LINE)

        // Then
        assertThat(result).isEqualTo(STATE)

    }

    @Test
    fun `should map Project entity to CSV line correctly`() {

        //Given / When
        val result = stateCsvMapper.mapTo(STATE)

        // Then
        assertThat(result).isEqualTo(CSV_STRING_LINE)
    }

    companion object {
        private val STATE = State(
            stateId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
            name = "In Progress"
        )

        private const val CSV_STRING_LINE = "123e4567-e89b-12d3-a456-426614174000,In Progress"

    }
}
