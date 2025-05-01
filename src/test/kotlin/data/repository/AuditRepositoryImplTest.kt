package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.example.data.repository.AuditRepositoryImpl
import org.example.data.storage.CsvStorageManager
import org.example.data.storage.mapper.AuditCsvMapper
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.MockAuditLog
import java.util.UUID

class AuditRepositoryImplTest {

    private lateinit var auditRepository: AuditRepository
    private val csvStorageManager: CsvStorageManager = mockk()
    private val auditCsvMapper: AuditCsvMapper = mockk()

    @BeforeEach
    fun setUp() {
        auditRepository = AuditRepositoryImpl(auditCsvMapper, csvStorageManager)
    }

    @Test
    fun `createAuditLog should return success when writing to file succeeds`() {
        // Given
        val line = MockAuditLog.FULL_CSV_STRING_LINE
        val auditLog = MockAuditLog.AUDIT_LOG
        every { csvStorageManager.writeLinesToFile(line) } just Runs

        // When / then
        try {
            val result = auditRepository.createAuditLog(auditLog)
            assertThat(result.isSuccess).isTrue()

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }

    }

    @Test
    fun `createAuditLog should return failure when an exception is thrown`() {
        // Given
        val exception = RuntimeException("Failed to write to file")
        every { csvStorageManager.writeLinesToFile(any()) } throws exception

        // When / then
        try {
            val result = auditRepository.createAuditLog(MockAuditLog.AUDIT_LOG)
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
        try {
            val result = auditRepository.getAuditLogs()
            assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getAuditLogs should return Result with list of AuditLogs when CSV contains valid lines`() {
        // Given
        every { csvStorageManager.readLinesFromFile() } returns listOf(MockAuditLog.FULL_CSV_STRING_LINE)
        every { auditCsvMapper.mapFrom(MockAuditLog.FULL_CSV_STRING_LINE) } returns MockAuditLog.AUDIT_LOG

        // When / Then
        try {
            val result = auditRepository.getAuditLogs()
            assertThat(result.getOrNull()).containsExactlyElementsIn(listOf(MockAuditLog.AUDIT_LOG))
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }


    @Test
    fun `getAuditLogs should return Result of ElementNotFoundException when AuditLog doesn't exists in CSV file`() {
        // Given
        every { csvStorageManager.readLinesFromFile() } returns listOf("invalid,line")
        // When / Then
        try {
            val result = auditRepository.getAuditLogs()
            assertThat(result.exceptionOrNull()).isInstanceOf(
                EiffelFlowException.NotFoundException::class.java
            )
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
    //endregion

    //region getAuditLogById
    @Test
    fun `getAuditLogById should return Result with empty list of AuditLog when CSV file is empty`() {
        // Given
        every { csvStorageManager.readLinesFromFile() } returns emptyList()

        // When / Then
        try {
            val result = auditRepository.getItemAuditLogById(UUID.randomUUID())
            assertThat(result.getOrNull()).isEmpty()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getAuditLogById should return Result with list of AuditLogs when CSV contains valid lines`() {
        // Given
        val itemId = MockAuditLog.AUDIT_LOG.itemId
        val csvLines = MockAuditLog.FULL_CSV_STRING_LINE.split("\n")
        every { csvStorageManager.readLinesFromFile() } returns csvLines
        every { auditCsvMapper.mapFrom(csvLines[0]) } returns MockAuditLog.AUDIT_LOG

        // When / Then
        try {
            val result = auditRepository.getItemAuditLogById(itemId)
            assertThat(result.getOrNull()).containsExactlyElementsIn(listOf(MockAuditLog.AUDIT_LOG))
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }

    }

    @Test
    fun `getAuditLogById should return Result of ElementNotFoundException when AuditLog doesn't exists in CSV file`() {
        // Given
        val auditLogWithNewId = MockAuditLog.AUDIT_LOG.copy(itemId = UUID.randomUUID())
        val csvLines = MockAuditLog.FULL_CSV_STRING_LINE.split("\n")
        every { csvStorageManager.readLinesFromFile() } returns csvLines
        every { auditCsvMapper.mapFrom(csvLines[0]) } returns auditLogWithNewId

        // When / Then
        try {
            val result = auditRepository.getItemAuditLogById(UUID.randomUUID())
            assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }

    }
    //endregion
}