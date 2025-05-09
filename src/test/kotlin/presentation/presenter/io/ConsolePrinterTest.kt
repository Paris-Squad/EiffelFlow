package presentation.presenter.io


import org.example.presentation.io.ConsolePrinter
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

class ConsolePrinterTest {

    @Test
    fun `should print the correct output when displayLn is called`() {
        val consolePrinter = ConsolePrinter()

        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        consolePrinter.displayLn("Hello, World!")

        assertEquals("Hello, World!", outputStream.toString().trim())
    }
}

