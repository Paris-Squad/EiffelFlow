package presentation.helper.extensions

import kotlinx.datetime.LocalDateTime
import org.example.presentation.helper.extensions.to12HourTimeString
import org.example.presentation.helper.extensions.toFormattedDateTime
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TimeExtensionKtTest {

    // Time formatting tests
    @Test
    fun `should format AM time correctly`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 9, 15)

        // When
        val result = time.to12HourTimeString()

        // Then
        assertEquals("9:15 AM", result)
    }

    @Test
    fun `should format PM time correctly`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 15, 45)

        // When
        val result = time.to12HourTimeString()

        // Then
        assertEquals("3:45 PM", result)
    }

    @Test
    fun `should format midnight as 12-00 AM`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 0, 0)

        // When
        val result = time.to12HourTimeString()

        // Then
        assertEquals("12:00 AM", result)
    }

    @Test
    fun `should format noon as 12-00 PM`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 12, 0)

        // When
        val result = time.to12HourTimeString()

        // Then
        assertEquals("12:00 PM", result)
    }

    @Test
    fun `should pad single digit minutes with zero`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 13, 5)

        // When
        val result = time.to12HourTimeString()

        // Then
        assertEquals("1:05 PM", result)
    }

    @Test
    fun `should handle 1 AM correctly`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 1, 0)

        // When
        val result = time.to12HourTimeString()

        // Then
        assertEquals("1:00 AM", result)
    }

    @Test
    fun `should handle 11 PM correctly`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 23, 0)

        // When
        val result = time.to12HourTimeString()

        // Then
        assertEquals("11:00 PM", result)
    }

    @Test
    fun `should formatted the date and time `() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 23, 0)

        // When
        val result = time.toFormattedDateTime()

        // Then
        assertEquals("2023-01-01 at 11:00 PM", result)
    }


}