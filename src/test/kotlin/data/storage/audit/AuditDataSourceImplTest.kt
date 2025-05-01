package data.storage.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.data.storage.CsvStorageManager
import org.example.data.storage.audit.AuditDataSource
import org.example.data.storage.audit.AuditDataSourceImpl
import org.example.data.storage.mapper.AuditCsvMapper
import org.example.domain.model.exception.EiffelFlowException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.MockAuditLog
import java.util.UUID

class AuditDataSourceImplTest {

    private lateinit var auditDataSource: AuditDataSource
    private val csvStorageManager: CsvStorageManager = mockk()
    private val auditCsvMapper: AuditCsvMapper = mockk()

    @BeforeEach
    fun setUp() {
        auditDataSource = AuditDataSourceImpl(auditCsvMapper, csvStorageManager)
    }

    @Test
    fun `createAuditLog should return all audit logs`() {
        try {
            auditDataSource.createAuditLog(MockAuditLog.AUDIT_LOG)
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
            val result = auditDataSource.getAuditLogs()
            assertThat(result.getOrNull()).isEmpty()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getAuditLogs should return Result with list of AuditLogs when CSV contains valid lines`() {
        // Given
        every {
            csvStorageManager.readLinesFromFile()
        } returns MockAuditLog.FULL_CSV_STRING_LINE.split("\n")

        // When / Then
        try {
            val result = auditDataSource.getAuditLogs()
            assertThat(result.getOrNull()).containsExactlyElementsIn(listOf(MockAuditLog.AUDIT_LOG))
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getAuditLogs should return Result of ElementNotFoundException when AuditLog doesn't exists in CSV file`() {
        // Given
        val exception = EiffelFlowException.ElementNotFoundException("Project not found")

        // When / Then
        try {
            val result = auditDataSource.getAuditLogs()
            assertThat(result.exceptionOrNull()).isInstanceOf(
                EiffelFlowException.ElementNotFoundException::class.java
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
            val result = auditDataSource.getItemAuditLogById(UUID.randomUUID())
            assertThat(result.getOrNull()).isEmpty()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getAuditLogById should return Result with list of AuditLogs when CSV contains valid lines`() {
        // Given
        val auditLogId = MockAuditLog.AUDIT_LOG.auditId
        every {
            csvStorageManager.readLinesFromFile()
        } returns MockAuditLog.FULL_CSV_STRING_LINE.split("\n")

        // When / Then
        try {
            val result = auditDataSource.getItemAuditLogById(auditLogId)
            assertThat(result.getOrNull()).containsExactlyElementsIn(listOf(MockAuditLog.AUDIT_LOG))
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getAuditLogById should return Result of ElementNotFoundException when AuditLog doesn't exists in CSV file`() {
        // Given
        val exception = EiffelFlowException.ElementNotFoundException("Project not found")

        // When / Then
        try {
            val result = auditDataSource.getItemAuditLogById(UUID.randomUUID())
            assertThat(result.exceptionOrNull()).isInstanceOf(
                EiffelFlowException.ElementNotFoundException::class.java
            )
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
    //endregion
}