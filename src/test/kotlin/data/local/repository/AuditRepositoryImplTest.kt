package data.local.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.example.data.local.csvrepository.AuditRepositoryImpl
import org.example.data.local.FileDataSource
import org.example.data.local.parser.AuditCsvParser
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.MockAuditLog
import utils.MockAuditLog.AUDIT_LOG
import utils.TaskMock
import java.util.*

class AuditRepositoryImplTest {

    private lateinit var auditRepository: AuditRepository
    private val csvStorageManager: FileDataSource = mockk()
    private val auditCsvParser: AuditCsvParser = mockk()
    private var taskRepositoryProvider: Lazy<TaskRepository> = mockk()

    @BeforeEach
    fun setUp() {
        auditRepository = AuditRepositoryImpl(
            auditCsvParser,
            csvStorageManager,
            taskRepositoryProvider
        )
    }

    @Test
    fun `createAuditLog should return success when writing to file succeeds`() {
        runTest {
            // Given
            val line = MockAuditLog.FULL_CSV_STRING_LINE
            val auditLog = AUDIT_LOG
            every { auditCsvParser.serialize(AUDIT_LOG) } returns line
            every { csvStorageManager.writeLinesToFile(line) } just Runs
            // When
            val result = auditRepository.createAuditLog(auditLog)
            // Then
            assertThat(result).isEqualTo(auditLog)
        }
    }

    @Test

    fun `createAuditLog should return failure when an exception is thrown`() {
        runTest {
            // Given
            val exception = RuntimeException("Failed to write to file")
            every { csvStorageManager.writeLinesToFile(any()) } throws exception

            // When / then
            assertThrows<RuntimeException> {
                auditRepository.createAuditLog(AUDIT_LOG)
            }
        }
    }


    @Test
    fun `getAuditLogs should return list of AuditLogs when CSV contains valid lines`() {
        runTest {
            // Given
            every { csvStorageManager.readLinesFromFile() } returns listOf(MockAuditLog.FULL_CSV_STRING_LINE)
            every { auditCsvParser.parseCsvLine(MockAuditLog.FULL_CSV_STRING_LINE) } returns AUDIT_LOG

            // When
            val result = auditRepository.getAuditLogs()

            // Then
            assertThat(result).containsExactlyElementsIn(listOf(AUDIT_LOG))
        }
    }

    @Test
    fun `getAuditLogs should skip null returns from parser`() {
        runTest {
            every { csvStorageManager.readLinesFromFile() } returns listOf("line1", "line2")
            every { auditCsvParser.parseCsvLine("line1") } returns AUDIT_LOG
            every { auditCsvParser.parseCsvLine("line2") } throws RuntimeException("invalid format")

            val result = auditRepository.getAuditLogs()

            assertThat(result).isEqualTo(listOf(AUDIT_LOG))
        }
    }


    //region getTaskAuditLogById
    @Test
    fun `getTaskAuditLogById should return empty list when CSV file is empty`() {
        runTest {
            // Given
            every { csvStorageManager.readLinesFromFile() } returns emptyList()

            // When / Then
            val result = auditRepository.getTaskAuditLogById(UUID.randomUUID())
            assertThat(result).isEqualTo(emptyList<AuditLog>())
        }
    }

    @Test
    fun `getTaskAuditLogById should return list of AuditLogs when CSV contains valid lines`() {
        runTest {
            // Given
            val itemId = AUDIT_LOG.itemId
            val csvLines = MockAuditLog.FULL_CSV_STRING_LINE.split("\n")
            every { csvStorageManager.readLinesFromFile() } returns csvLines
            every { auditCsvParser.parseCsvLine(csvLines[0]) } returns AUDIT_LOG

            // When / Then
            val result = auditRepository.getTaskAuditLogById(itemId)
            assertThat(result).containsExactlyElementsIn(listOf(AUDIT_LOG))
        }
    }

    //endregion


    @Test
    fun `getProjectAuditLogById should return list of AuditLogs when CSV contains valid lines`() {
        runTest {
            // Given
            val projectId = UUID.randomUUID()
            val taskId = UUID.randomUUID()

            val validTask = TaskMock.validTask.copy(
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
            coEvery { taskRepositoryProvider.value.getTasks() } returns listOf(validTask)

            // When
            val result = auditRepository.getProjectAuditLogById(projectId)

            // Then
            assertThat(result).isEqualTo(listOf(expectedAuditLog))
        }
    }

    @Test
    fun `getProjectAuditLogById should throw NotFoundException when taskRepository fails`() {
        runTest {
            // Given
            val csvLines = listOf(MockAuditLog.FULL_CSV_STRING_LINE)
            every { csvStorageManager.readLinesFromFile() } returns csvLines
            coEvery { taskRepositoryProvider.value.getTasks() } throws EiffelFlowException
                .NotFoundException("No audit logs found for project or related tasks:${TaskMock.validTask.projectId}")


            // When / Then

            assertThrows<EiffelFlowException.NotFoundException> {
                auditRepository.getProjectAuditLogById(UUID.randomUUID())
            }
        }
    }

    @Test
    fun `getProjectAuditLogById should return logs if audit log belongs to task in the project`() {
        runTest {
            // Given
            val projectId = UUID.randomUUID()
            val taskId = UUID.randomUUID()

            val auditLog = AUDIT_LOG.copy(itemId = taskId)
            val csvLines = listOf(MockAuditLog.FULL_CSV_STRING_LINE)

            every { csvStorageManager.readLinesFromFile() } returns csvLines
            coEvery { taskRepositoryProvider.value.getTasks() } returns listOf(TaskMock.validTask.copy(taskId = taskId, projectId = projectId))


            every { auditCsvParser.parseCsvLine(csvLines[0]) } returns auditLog

            // When
            val result = auditRepository.getProjectAuditLogById(projectId)

            // Then
            assertThat(result).isEqualTo(listOf(auditLog))
        }
    }

    @Test
    fun `getProjectAuditLogById should skip invalid CSV lines gracefully`() {
        runTest {
            // Given
            val projectId = UUID.randomUUID()
            val taskId = UUID.randomUUID()

            val auditLog = AUDIT_LOG.copy(itemId = taskId)
            val task = TaskMock.validTask.copy(taskId = taskId, projectId = projectId)

            every { csvStorageManager.readLinesFromFile() } returns listOf("validLine", "invalidLine")
            every { auditCsvParser.parseCsvLine("validLine") } returns auditLog
            every { auditCsvParser.parseCsvLine("invalidLine") } throws IllegalArgumentException()
            coEvery { taskRepositoryProvider.value.getTasks() } returns listOf(task)
            // When
            val result = auditRepository.getProjectAuditLogById(projectId)

            // Then
            assertThat(result).isEqualTo(listOf(auditLog))
        }
    }


    @Test
    fun `getProjectAuditLogById should throw when RuntimeException unexpected exception is thrown`() {
        runTest {
            // Given
            every { csvStorageManager.readLinesFromFile() } throws RuntimeException("unexpected")

            // When / Then
            assertThrows<RuntimeException> {
                auditRepository.getProjectAuditLogById(UUID.randomUUID())
            }
        }
    }

    @Test
    fun `getProjectAuditLogById should throw IOException when fileDataSource throws exception`() {
        runTest {
            // Given
            every { csvStorageManager.readLinesFromFile() } throws EiffelFlowException.IOException("file error")

            // When / Then
            assertThrows<EiffelFlowException.IOException> {
                auditRepository.getProjectAuditLogById(UUID.randomUUID())
            }
        }
    }

    @Test
    fun `getProjectAuditLogById should return logs sorted by auditTime descending`() {
        runTest {
            // Given
            val projectId = UUID.randomUUID()
            val taskId1 = UUID.randomUUID()
            val taskId2 = UUID.randomUUID()

            val log1 = AUDIT_LOG.copy(itemId = taskId1, auditTime = LocalDateTime.parse("2023-01-01T10:00"))
            val log2 = AUDIT_LOG.copy(itemId = taskId2, auditTime = LocalDateTime.parse("2023-01-01T12:00"))

            val csvLines = listOf("logLine1", "logLine2")

            every { csvStorageManager.readLinesFromFile() } returns csvLines
            every { auditCsvParser.parseCsvLine("logLine1") } returns log1
            every { auditCsvParser.parseCsvLine("logLine2") } returns log2
            coEvery { taskRepositoryProvider.value.getTasks() } returns listOf(
                TaskMock.validTask.copy(taskId = taskId1, projectId = projectId),
                TaskMock.validTask.copy(taskId = taskId2, projectId = projectId)
            )

            // When
            val result = auditRepository.getProjectAuditLogById(projectId)

            // Then
            assertThat(result).containsExactly(log2, log1)
        }
    }


    //endregion
}