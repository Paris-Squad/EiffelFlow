package data.local

import com.google.common.truth.Truth
import org.example.data.local.FileDataSource
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class CsvStorageManagerTest {

    @TempDir
    lateinit var tempDir: File

    @Throws(FileNotFoundException::class)
    @Test
    fun `should throw FileNotFoundException when file does not exist`() {
        // Given
        val nonExistentFile = File(tempDir, "notExistFile.csv")
        val csvStorageManager = FileDataSource(nonExistentFile)

        // When
        val exception = assertThrows<FileNotFoundException> {
            csvStorageManager.readLinesFromFile()
        }

        // Then
        Truth.assertThat("File ${nonExistentFile.path} not found.").isEqualTo(exception.message)
    }

    @Throws(FileNotFoundException::class)
    @Test
    fun `should throw FileNotFoundException when path is a directory`() {
        // Given
        val directory = tempDir.resolve("directory").apply { mkdir() }
        val csvStorageManager = FileDataSource(directory)

        // When / Then
        val exception = assertThrows<FileNotFoundException> {
            csvStorageManager.readLinesFromFile()
        }
        Truth.assertThat("${directory.path} Is a directory").isEqualTo(exception.message)
    }

    @Test
    fun `should return list of lines that exist in the file when file exists`() {
        // Given
        val testFile = File(tempDir, "multiple_lines.csv").apply {
            writeText(DUMMY_FILE_CONTENT)
        }
        val csvStorageManager = FileDataSource(testFile)

        // When
        val result = csvStorageManager.readLinesFromFile()

        // Then
        Truth.assertThat(DUMMY_FILE_CONTENT.split("\n")).containsExactlyElementsIn(result)
    }

    @Test
    fun `should return list of only one line that exist in the file when the file exists`() {
        // Given
        val fileContent = "single line"
        val testFile = File(tempDir, "single_line.csv").apply {
            writeText(fileContent)
        }
        val csvStorageManager = FileDataSource(testFile)

        // When
        val result = csvStorageManager.readLinesFromFile()

        // Then
        Truth.assertThat(listOf(fileContent)).containsExactlyElementsIn(result)
    }


    @Test
    fun `writeLinesToFile should append text to file`() {
        //Given
        val testFile = File(tempDir, "write_file.csv")
        val csvStorageManager = FileDataSource(testFile)

        //Then
        csvStorageManager.writeLinesToFile(DUMMY_FILE_CONTENT)
        val result = csvStorageManager.readLinesFromFile()

        //When
        Truth.assertThat(DUMMY_FILE_CONTENT.split("\n")).containsExactlyElementsIn(result)
    }

    @Test
    fun `updateLinesToFile should replace line in file when line exists`() {
        val initialContent = "line1\nline2\nline3"
        val testFile = File(tempDir, "update_file.csv").apply {
            writeText(initialContent)
        }
        val csvStorageManager = FileDataSource(testFile)
        val oldLine = "line2"
        val newLine = "updated line"

        // When
        csvStorageManager.updateLinesToFile(newLine, oldLine)

        // Then
        val expectedContent = "line1\nupdated line\nline3"
        val result = csvStorageManager.readLinesFromFile()
        Truth.assertThat(expectedContent.split("\n")).containsExactlyElementsIn(result)
    }

    @Throws(IOException::class)
    @Test
    fun `updateLinesToFile should threw IOException when line does not exist`() {
        // Given
        val initialContent = "line1\nline2\nline3"
        val testFile = File(tempDir, "no_change_file.csv").apply {
            writeText(initialContent)
        }
        val csvStorageManager = FileDataSource(testFile)
        val nonExistentLine = "line4"
        val newLine = "updated line"

        //when and Then
        assertThrows<IOException> {
            csvStorageManager.updateLinesToFile(newLine, nonExistentLine)
        }
    }

    @Test
    fun `should throw FileNotFoundException when file does not exist for clearFile`() {
        // Given
        val nonExistentFile = File(tempDir, "notExistFile.csv")
        val csvStorageManager = FileDataSource(nonExistentFile)

        // When/Then
        assertThrows<FileNotFoundException> {
            csvStorageManager.clearFile()
        }
    }

    @Test
    fun `should clear content when file exists for clearFile`() {
        // Given
        val testFile = File(tempDir, "file_to_clear.csv").apply {
            writeText(DUMMY_FILE_CONTENT)
        }
        val csvStorageManager = FileDataSource(testFile)

        // When
        csvStorageManager.clearFile()

        // Then
        Truth.assertThat(testFile.readText()).isEmpty()
    }

    @Test
    fun `deleteLineFromFile should remove line from file when line exists`() {
        // Given
        val initialContent = "line1\nline2\nline3"
        val testFile = File(tempDir, "delete_line_file.csv").apply {
            writeText(initialContent)
        }
        val csvStorageManager = FileDataSource(testFile)
        val lineToDelete = "line2"

        // When
        csvStorageManager.deleteLineFromFile(lineToDelete)

        // Then
        val expectedContent = "line1\nline3"
        val result = csvStorageManager.readLinesFromFile()
        Truth.assertThat(expectedContent.split("\n")).containsExactlyElementsIn(result)
    }

    @Test
    fun `deleteLineFromFile should throw IOException when line does not exist`() {
        // Given
        val initialContent = "line1\nline2\nline3"
        val testFile = File(tempDir, "nonexistent_line_file.csv").apply {
            writeText(initialContent)
        }
        val csvStorageManager = FileDataSource(testFile)
        val nonExistentLine = "line4"

        // When/Then
        val exception = assertThrows<IOException> {
            csvStorageManager.deleteLineFromFile(nonExistentLine)
        }
        Truth.assertThat(exception.message).isEqualTo("Line not found in file.")

        // Verify file content was not modified
        val result = csvStorageManager.readLinesFromFile()
        Truth.assertThat(initialContent.split("\n")).containsExactlyElementsIn(result)
    }

    companion object {
        private const val DUMMY_FILE_CONTENT =
            "02ad4499-5d4c-4450-8fd1-8294f1bb5748,In Progress\n02ad4499-5d4c-4450-8fd1-8294f1bb5748,In Progress"
    }
}