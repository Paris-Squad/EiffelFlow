package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.data.repository.AuditRepositoryImpl
import org.example.data.storage.audit.AuditDataSource
import org.example.domain.model.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.MockAuditLog
import java.util.UUID

class AuditRepositoryImplTest {

    private val auditDataSource: AuditDataSource = mockk()
    private lateinit var auditRepository: AuditRepository

    @BeforeEach
    fun setUp() {
        auditRepository = AuditRepositoryImpl(auditDataSource)
    }

    //region getAuditLogs
    @Test
    fun `getAuditLogs should return Result of empty list of AuditLog when there is no AuditLogs in data source`() {
        // Given
        every { auditDataSource.getAuditLogs() } returns Result.success(emptyList())

        // When / Then
        try {
            val result = auditRepository.getAuditLogs()
            assertThat(result.getOrNull()).isEmpty()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getAuditLogs should return Result with AuditLog list when there is AuditLogs exist in data source`() {
        // Given
        every {
            auditDataSource.getAuditLogs()
        } returns Result.success(listOf(MockAuditLog.AUDIT_LOG))

        // When / Then
        try {
            val result = auditRepository.getAuditLogs()
            assertThat(result.getOrNull()).containsExactlyElementsIn(listOf(MockAuditLog.AUDIT_LOG))
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getAuditLogs should should return Result of ElementNotFoundException when there is no AuditLogs exist in data source`() {
        // Given
        val exception = EiffelFlowException.ElementNotFoundException("Project not found")

        // When / Then
        try {
            val result = auditRepository.getAuditLogs()
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
    fun `getAuditLogById should return Result with AuditLog list when there is AuditLogs exist in data source`() {
        // Given
        every {
            auditDataSource.getItemAuditLogById(MockAuditLog.AUDIT_LOG.auditId)
        } returns Result.success(listOf(MockAuditLog.AUDIT_LOG))

        // When / Then
        try {
            val result = auditRepository.getItemAuditLogById(MockAuditLog.AUDIT_LOG.auditId)
            assertThat(result.getOrNull()).containsExactlyElementsIn(listOf(MockAuditLog.AUDIT_LOG))
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getAuditLogById should return Result with ElementNotFoundException when AuditLog doesn't exists in data source`() {
        // Given
        val exception = EiffelFlowException.ElementNotFoundException("Project not found")
        every {
            auditDataSource.getItemAuditLogById(any())
        } returns Result.failure(exception)

        // When / Then
        try {
            val result = auditRepository.getItemAuditLogById(UUID.randomUUID())
            assertThat(result.exceptionOrNull()).isInstanceOf(
                EiffelFlowException.ElementNotFoundException::class.java
            )
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
    //endregion
}