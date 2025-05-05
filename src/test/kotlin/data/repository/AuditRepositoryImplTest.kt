package data.repository

import com.google.common.truth.Truth.assertThat
import domain.usecase.task.TaskMock.validTask
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.example.data.repository.AuditRepositoryImpl
import org.example.data.storage.FileDataSource
import org.example.data.storage.parser.AuditCsvParser
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.MockAuditLog
import utils.MockAuditLog.AUDIT_LOG
import java.util.*

class AuditRepositoryImplTest {

    private lateinit var auditRepository: AuditRepository
    private val csvStorageManager: FileDataSource = mockk()
    private val auditCsvParser: AuditCsvParser = mockk()
    private val taskRepository: TaskRepository = mockk()

    @BeforeEach
    fun setUp() {
        auditRepository = AuditRepositoryImpl(auditCsvParser, csvStorageManager, taskRepository)
    }

    @Test
    fun `createAuditLog should return success when writing to file succeeds`() {
        // Given
        val line = MockAuditLog.FULL_CSV_STRING_LINE
        val auditLog = AUDIT_LOG
        every { csvStorageManager.writeLinesToFile(line) } just Runs

        // When / then
            val result = auditRepository.createAuditLog(auditLog)
            assertThat(result.isSuccess).isTrue()

    }

    @Test
    fun `createAuditLog should return failure when an exception is thrown`() {
        // Given
        val exception = RuntimeException("Failed to write to file")
        every { csvStorageManager.writeLinesToFile(any()) } throws exception

        // When / then
        try {
            val result = auditRepository.createAuditLog(AUDIT_LOG)
            assertThat(result.isFailure).isTrue()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }

    }

    //region getAuditLogs
    @Test
    fun `getAuditLogs should return Result with empty list of AuditLog when CSV file is empty`() {
        // Given
        every { csvStorageManager.readLinesFromFile() } returns emptyList()

        // When / Then
            val result = auditRepository.getAuditLogs()
            assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }

    @Test
    fun `getAuditLogs should return Result with list of AuditLogs when CSV contains valid lines`() {
        // Given
        every { csvStorageManager.readLinesFromFile() } returns listOf(MockAuditLog.FULL_CSV_STRING_LINE)
        every { auditCsvParser.parseCsvLine(MockAuditLog.FULL_CSV_STRING_LINE) } returns AUDIT_LOG

        // When / Then
            val result = auditRepository.getAuditLogs()
            assertThat(result.getOrNull()).containsExactlyElementsIn(listOf(AUDIT_LOG))

    }


    @Test
    fun `getAuditLogs should return Result of ElementNotFoundException when AuditLog doesn't exists in CSV file`() {
        // Given
        every { csvStorageManager.readLinesFromFile() } returns listOf("invalid,line")
        // When / Then
            val result = auditRepository.getAuditLogs()
            assertThat(result.exceptionOrNull()).isInstanceOf(
                EiffelFlowException.NotFoundException::class.java
            )

    }
    //endregion

    //region getTaskAuditLogById
    @Test
    fun `getTaskAuditLogById should return Result with empty list of AuditLog when CSV file is empty`() {
        // Given
        every { csvStorageManager.readLinesFromFile() } returns emptyList()

        // When / Then
        val result = auditRepository.getTaskAuditLogById(UUID.randomUUID())
        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `getTaskAuditLogById should return Result with list of AuditLogs when CSV contains valid lines`() {
        // Given
        val itemId = AUDIT_LOG.itemId
        val csvLines = MockAuditLog.FULL_CSV_STRING_LINE.split("\n")
        every { csvStorageManager.readLinesFromFile() } returns csvLines
        every { auditCsvParser.parseCsvLine(csvLines[0]) } returns AUDIT_LOG

        // When / Then
        val result = auditRepository.getTaskAuditLogById(itemId)
        assertThat(result.getOrNull()).containsExactlyElementsIn(listOf(AUDIT_LOG))


    }

    @Test
    fun `getTaskAuditLogById should return Result of ElementNotFoundException when AuditLog doesn't exists in CSV file`() {
        // Given
        val auditLogWithNewId = AUDIT_LOG.copy(itemId = UUID.randomUUID())
        val csvLines = MockAuditLog.FULL_CSV_STRING_LINE.split("\n")
        every { csvStorageManager.readLinesFromFile() } returns csvLines
        every { auditCsvParser.parseCsvLine(csvLines[0]) } returns auditLogWithNewId

        // When / Then
        val result = auditRepository.getTaskAuditLogById(UUID.randomUUID())
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }

    //endregion
    //region getProjectAuditLogById
    @Test
    fun `getProjectAuditLogById should return Result with empty list of AuditLog when CSV file is empty`() {
        // Given
        every { csvStorageManager.readLinesFromFile() } returns emptyList()

        // When / Then
        val result = auditRepository.getProjectAuditLogById(UUID.randomUUID())
        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `getProjectAuditLogById should return Result with list of AuditLogs when CSV contains valid lines`() {
        // Given
        val projectId = UUID.randomUUID()
        val taskId = UUID.randomUUID()

        val validTask = validTask.copy(
            taskId = taskId,
            projectId = projectId,
        )

        val expectedAuditLog = AUDIT_LOG.copy(
            itemId = taskId
        )

        val csvLine = "UPDATED,$taskId,${expectedAuditLog.userId}"
        val csvLines = listOf(csvLine)

        every { csvStorageManager.readLinesFromFile() } returns csvLines
        every { auditCsvParser.parseCsvLine(csvLine) } returns expectedAuditLog
        every { taskRepository.getTasks() } returns listOf(validTask)

        // When
        val result = auditRepository.getProjectAuditLogById(projectId)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).containsExactly(expectedAuditLog)
    }


    @Test
    fun `getProjectAuditLogById should return Result of ElementNotFoundException when AuditLog doesn't exists in CSV file`() {
        // Given
        val auditLogWithNewId = AUDIT_LOG.copy(itemId = UUID.randomUUID())
        val csvLines = MockAuditLog.FULL_CSV_STRING_LINE.split("\n")
        every { csvStorageManager.readLinesFromFile() } returns csvLines
        every { auditCsvParser.parseCsvLine(csvLines[0]) } returns auditLogWithNewId
        every { taskRepository.getTasks() } returns listOf(validTask, validTask)

        // When / Then
        val result = auditRepository.getProjectAuditLogById(UUID.randomUUID())
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }

    @Test
    fun `getProjectAuditLogById should return failure when taskRepository fails`() {
        // Given
        val csvLines = listOf(MockAuditLog.FULL_CSV_STRING_LINE)
        every { csvStorageManager.readLinesFromFile() } returns csvLines
        every { taskRepository.getTasks() } throws  EiffelFlowException
                    .NotFoundException("No audit logs found for project or related tasks:${validTask.projectId}")


        // When
        val result = auditRepository.getProjectAuditLogById(UUID.randomUUID())

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).hasMessageThat()
            .contains("No audit logs found for project or related tasks:${validTask.projectId}")
    }

    @Test
    fun `getProjectAuditLogById should return logs if audit log belongs to task in the project`() {
        // Given
        val projectId = UUID.randomUUID()
        val taskId = UUID.randomUUID()

        val auditLog = AUDIT_LOG.copy(itemId = taskId)
        val csvLines = listOf(MockAuditLog.FULL_CSV_STRING_LINE)

        every { csvStorageManager.readLinesFromFile() } returns csvLines
        every { taskRepository.getTasks() } returns listOf(validTask.copy(taskId = taskId, projectId = projectId))


        every { auditCsvParser.parseCsvLine(csvLines[0]) } returns auditLog

        // When
        val result = auditRepository.getProjectAuditLogById(projectId)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).containsExactly(auditLog)
    }

    @Test
    fun `getProjectAuditLogById should skip invalid CSV lines gracefully`() {
        // Given
        val projectId = UUID.randomUUID()
        val taskId = UUID.randomUUID()

        val auditLog = AUDIT_LOG.copy(itemId = taskId)
        val task = validTask.copy(taskId = taskId, projectId = projectId)

        every { csvStorageManager.readLinesFromFile() } returns listOf("validLine", "invalidLine")
        every { auditCsvParser.parseCsvLine("validLine") } returns auditLog
        every { auditCsvParser.parseCsvLine("invalidLine") } throws IllegalArgumentException()
        every { taskRepository.getTasks() } returns listOf(task)
        // When
        val result = auditRepository.getProjectAuditLogById(projectId)
        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).containsExactly(auditLog)
    }


    @Test
    fun `getProjectAuditLogById should return failure when unexpected exception is thrown`() {
        // Given
        every { csvStorageManager.readLinesFromFile() } throws RuntimeException("unexpected")

        // When
        val result = auditRepository.getProjectAuditLogById(UUID.randomUUID())

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).hasMessageThat().contains("unexpected")
    }

    @Test
    fun `getProjectAuditLogById should return failure when fileDataSource throws exception`() {
        // Given
        every { csvStorageManager.readLinesFromFile() } throws RuntimeException("file error")
        // When
        val result = auditRepository.getProjectAuditLogById(UUID.randomUUID())
        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).hasMessageThat().contains("file error")
    }

    //endregion
}