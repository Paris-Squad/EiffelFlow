package presentation.audit

import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.example.domain.model.AuditLogAction
import org.example.domain.usecase.audit.GetTaskAuditUseCase
import org.example.presentation.audit.GetTaskAuditLogsCLI
import org.example.presentation.helper.extensions.toFormattedDateTime
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.junit.jupiter.api.Test
import utils.MockAuditLog
import java.util.UUID

class GetTaskAuditLogsCLITest {
    private val getTaskAuditLogsUseCase: GetTaskAuditUseCase = mockk()
    private val inputReader: InputReader = mockk()
    private val printer: Printer = mockk()
    private val cli = GetTaskAuditLogsCLI(getTaskAuditLogsUseCase, inputReader, printer)

    private val validTaskId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
    private val sampleAuditLog = MockAuditLog.AUDIT_LOG.copy(
        itemId = validTaskId,
        itemName = "Test Task",
        auditTime = LocalDateTime(2023, 1, 1, 14, 30)
    )

    // success
    @Test
    fun `should display task audit logs for valid task ID`() {
        // Given
        every { inputReader.readString() } returns validTaskId.toString()
        every { printer.displayLn(any()) } just Runs
        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns listOf(sampleAuditLog)
        // When
        cli.start()
        // Then
        verify { printer.displayLn("Enter Task ID to retrieve audit logs:") }
        verify { printer.displayLn("[Task] Created 'Test Task'") }
        verify { printer.displayLn("  Date & Time     : ${sampleAuditLog.auditTime.toFormattedDateTime()}")
        }
    }

    @Test
    fun `should show all log details when printing audit entries`() = runBlocking {
        // Given
        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns listOf(sampleAuditLog)
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getAuditLogsForTask(validTaskId)
        // Then
        verify {
            printer.displayLn("[Task] Created 'Test Task'")
            printer.displayLn("  Audit ID        : ${sampleAuditLog.auditId}")
            printer.displayLn("  Date & Time     : ${sampleAuditLog.auditTime.toFormattedDateTime()}")
            printer.displayLn("  Modified By     : ${sampleAuditLog.editorName}")
            printer.displayLn("  Field Changed   : ${sampleAuditLog.changedField ?: "Not Available"}")
            printer.displayLn("  Old             : ${sampleAuditLog.oldValue ?: "Not Available"}")
            printer.displayLn("  New             : ${sampleAuditLog.newValue ?: "Not Available"}")
            printer.displayLn("-".repeat(50))
        }
    }

    @Test
    fun `should label log as Task when itemId is different from projectId`() = runBlocking {
        // Given
        val differentLog = sampleAuditLog.copy(itemId = UUID.randomUUID())
        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns listOf(differentLog)
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getAuditLogsForTask(validTaskId)
        // Then
        verify { printer.displayLn("[Task] Created '${differentLog.itemName}'") }
    }

    @Test
    fun `should display Updated when log is UPDATE`() = runBlocking {
        // Given
        val updateLog = sampleAuditLog.copy(actionType = AuditLogAction.UPDATE)

        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns listOf(updateLog)
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getAuditLogsForTask(validTaskId)
        // Then
        verify { printer.displayLn("[Task] Updated '${updateLog.itemName}'") }
    }

    @Test
    fun `should display Deleted when log is DELETE`() = runBlocking {
        // Given
        val deleteLog = sampleAuditLog.copy(actionType = AuditLogAction.DELETE)
        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns listOf(deleteLog)
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getAuditLogsForTask(validTaskId)
        // Then
        verify { printer.displayLn("[Task] Deleted '${deleteLog.itemName}'") }
    }

    @Test
    fun `should get all log details when printing audit entries`() = runBlocking {
        // Given
        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns listOf(sampleAuditLog)
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getAuditLogsForTask(validTaskId)
        // Then
        coVerify { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) }

        verify { printer.displayLn("  Audit ID        : ${sampleAuditLog.auditId}") }
        verify { printer.displayLn("  Date & Time     : ${sampleAuditLog.auditTime.toFormattedDateTime()}") }
        verify { printer.displayLn("  Modified By     : ${sampleAuditLog.editorName}") }
        verify { printer.displayLn("  Field Changed   : ${sampleAuditLog.changedField}") }
        verify { printer.displayLn("  Old             : ${sampleAuditLog.oldValue}") }
        verify { printer.displayLn("  New             : ${sampleAuditLog.newValue}") }
    }


    // show error messages
    @Test
    fun `should show error formatting message when Task ID has invalid format`() {
        // Given
        every { inputReader.readString() } returns "invalid-uuid"
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getTaskAuditLogsInput()
        // Then
        verify { printer.displayLn("Invalid Task ID format. Please enter a valid UUID.") }
    }

    @Test
    fun `should show error not found message when Task has no logs`() = runBlocking {
        // Given
        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns emptyList()
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getAuditLogsForTask(validTaskId)
        // Then
        verify { printer.displayLn("No audit logs found for Task ID: $validTaskId.") }
    }

    @Test
    fun `should show Not Available when changedField, oldValue and newValue are null`() = runBlocking {
        // Given
        val logWithNulls = sampleAuditLog.copy(
            changedField = null,
            oldValue = null,
            newValue = null
        )

        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns listOf(logWithNulls)
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getAuditLogsForTask(validTaskId)
        // Then
        verify { printer.displayLn("  Field Changed   : Not Available") }
        verify { printer.displayLn("  Old             : Not Available") }
        verify { printer.displayLn("  New             : Not Available") }
    }

    @Test
    fun `should show error invalid UUID message when Task ID is empty or null or blank`() {
        listOf(null, "", "   ").forEach {
            // Given
            every { inputReader.readString() } returns it
            every { printer.displayLn(any()) } just Runs
            // When
            cli.start()
            // Then
            verify { printer.displayLn("Task ID cannot be empty. Please provide a valid UUID.") }
            clearMocks(printer)
        }
    }

    @Test
    fun `should handle empty audit logs gracefully`() = runBlocking {
        // Given
        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns emptyList()
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getAuditLogsForTask(validTaskId)
        // Then
        coVerify { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) }
        verify { printer.displayLn("No audit logs found for Task ID: $validTaskId.") }
    }
}
