package data.storge.mapper

import com.google.common.truth.Truth.assertThat
import org.example.data.storge.mapper.StateCsvMapper
import org.example.domain.model.entities.State
import java.util.UUID
import kotlin.test.Test

class StateCsvMapperTest {

    private val stateCsvMapper = StateCsvMapper()

    @Test
    fun `test mapFrom CSV to State`() {
        val csv = "123e4567-e89b-12d3-a456-426614174000,In Progress"

        try {
            val state = stateCsvMapper.mapFrom(csv)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `test mapTo State to CSV`() {
        val state = State(
            stateId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
            name = "In Progress"
        )

        try {
            val csv = stateCsvMapper.mapTo(state)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}
