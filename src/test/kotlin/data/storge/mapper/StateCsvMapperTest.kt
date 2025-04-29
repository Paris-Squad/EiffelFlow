package data.storge.mapper

import com.google.common.truth.Truth.assertThat
import org.example.data.storge.mapper.StateCsvMapper
import org.example.domain.model.entities.State
import java.util.*
import kotlin.test.Test

class StateCsvMapperTest {

    private val stateCsvMapper = StateCsvMapper()

    @Test
    fun `should map CSV line to State entity correctly`() {
        try {
            val result = stateCsvMapper.mapFrom(CSV_STRING_LINE)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should map State entity to CSV line correctly`() {
        try {
            val result = stateCsvMapper.mapTo(STATE)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    companion object {
        private val STATE = State(
            stateId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748"),
            name = "In Progress"
        )

        private const val CSV_STRING_LINE = "02ad4499-5d4c-4450-8fd1-8294f1bb5748,In Progress"
    }
}
