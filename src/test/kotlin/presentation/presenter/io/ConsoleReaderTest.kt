package presentation.presenter.io


import org.example.presentation.io.ConsoleReader
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConsoleReaderTest {

    @Test
    fun `should return correct input when readString is called`() {
        val consoleReader = ConsoleReader()

        val input = "test input"
        val originalIn = System.`in`
        System.setIn(input.byteInputStream())

        val result = consoleReader.readString()

        assertEquals(input, result)

        System.setIn(originalIn)
    }
}
