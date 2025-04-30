package data.storage

import com.google.common.truth.Truth.assertThat
import org.example.data.storage.CsvStorageManager
import org.example.domain.model.exception.EiffelFlowException

import org.junit.jupiter.api.*
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.FileNotFoundException

class CsvStorageManagerTest {

    @TempDir
    lateinit var tempDir: File

    @Throws(FileNotFoundException::class)
    @Test
    fun `should throw FileNotFoundException when file does not exist`() {
        // Given
        val nonExistentFile = File(tempDir, "notExistFile.csv")
        val csvStorageManager = CsvStorageManager(nonExistentFile)

        // When
        val exception = assertThrows<FileNotFoundException> {
            csvStorageManager.readLinesFromFile()
        }

        // Then
        assertThat("File ${nonExistentFile.path} not found.").isEqualTo(exception.message)
    }

    @Throws(FileNotFoundException::class)
    @Test
    fun `should throw FileNotFoundException when path is a directory`() {
        // Given
        val directory = tempDir.resolve("directory").apply { mkdir() }
        val csvStorageManager = CsvStorageManager(directory)

        // When / Then
        val exception = assertThrows<FileNotFoundException> {
            csvStorageManager.readLinesFromFile()
        }
        assertThat("${directory.path} Is a directory").isEqualTo(exception.message)
    }

    @Test
    fun `should return list of lines that exist in the file when file exists`() {
        // Given
        val testFile = File(tempDir, "multiple_lines.csv").apply {
            writeText(DUMMY_FILE_CONTENT)
        }
        val csvStorageManager = CsvStorageManager(testFile)

        // When
        val result = csvStorageManager.readLinesFromFile()

        // Then
        assertThat(DUMMY_FILE_CONTENT.split("\n")).containsExactlyElementsIn(result)
    }

    @Test
    fun `should return list of only one line that exist in the file when the file exists`() {
        // Given
        val fileContent = "single line"
        val testFile = File(tempDir, "single_line.csv").apply {
            writeText(fileContent)
        }
        val csvStorageManager = CsvStorageManager(testFile)

        // When
        val result = csvStorageManager.readLinesFromFile()

        // Then
        assertThat(listOf(fileContent)).containsExactlyElementsIn(result)
    }


    @Test
    fun `writeLinesToFile should append text to file`() {
        //Given
        val testFile = File(tempDir, "write_file.csv")
        val csvStorageManager = CsvStorageManager(testFile)

        //Then
        csvStorageManager.writeLinesToFile(DUMMY_FILE_CONTENT)
        val result = csvStorageManager.readLinesFromFile()

        //When
        assertThat(DUMMY_FILE_CONTENT.split("\n")).containsExactlyElementsIn(result)
    }

    @Test
    fun `updateLinesToFile should replace line in file when line exists`() {
        val initialContent = "line1\nline2\nline3"
        val testFile = File(tempDir, "update_file.csv").apply {
            writeText(initialContent)
        }
        val csvStorageManager = CsvStorageManager(testFile)
        val oldLine = "line2"
        val newLine = "updated line"

        // When
        csvStorageManager.updateLinesToFile(newLine, oldLine)

        // Then
        val expectedContent = "line1\nupdated line\nline3"
        val result = csvStorageManager.readLinesFromFile()
        assertThat(expectedContent.split("\n")).containsExactlyElementsIn(result)
    }

    @Throws(EiffelFlowException.LineNotFoundException::class)
    @Test
    fun `updateLinesToFile should threw LineNotFoundException when line does not exist`() {
        // Given
        val initialContent = "line1\nline2\nline3"
        val testFile = File(tempDir, "no_change_file.csv").apply {
            writeText(initialContent)
        }
        val csvStorageManager = CsvStorageManager(testFile)
        val nonExistentLine = "line4"
        val newLine = "updated line"

        //when and Then
        assertThrows<EiffelFlowException.LineNotFoundException> {
            csvStorageManager.updateLinesToFile(newLine, nonExistentLine)
        }
    }

    companion object {
        private const val DUMMY_FILE_CONTENT =
            "02ad4499-5d4c-4450-8fd1-8294f1bb5748,In Progress\n02ad4499-5d4c-4450-8fd1-8294f1bb5748,In Progress"
    }
}
