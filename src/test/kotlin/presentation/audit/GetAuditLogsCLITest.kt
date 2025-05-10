package presentation.audit

import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.example.domain.model.AuditLogAction
import org.example.domain.usecase.audit.GetAllAuditLogsUseCase
import org.example.presentation.audit.GetAuditLogsCLI
import org.example.presentation.io.Printer
import org.junit.jupiter.api.Test
import utils.MockAuditLog
import java.util.UUID
import kotlin.test.assertEquals

class GetAuditLogsCLITest {
    private val getAuditLogsUseCase: GetAllAuditLogsUseCase = mockk()
    private val printer: Printer = mockk()
    private val cli = GetAuditLogsCLI(getAuditLogsUseCase, printer)

    private val sampleAuditLog = MockAuditLog.AUDIT_LOG.copy(
        itemName = "Test Task",
        auditTime = LocalDateTime(2023, 1, 1, 14, 30)
    )

    // Success cases
    @Test
    fun `should show all log details when printing audit entries`() = runBlocking {
        // Given
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(sampleAuditLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.getAllAuditLogs()

        // Then
        verifySequence {
            printer.displayLn("=== Audit Logs Overview ===")
            printer.displayLn("----- [Audit] Created: Test Task -----")
            printer.displayLn("  Audit ID       : ${sampleAuditLog.auditId}")
            printer.displayLn("  Date           : ${sampleAuditLog.auditTime.date} / Time: ${cli.formatTime(sampleAuditLog.auditTime)}")
            printer.displayLn("  Modified By    : ${sampleAuditLog.editorName}")
            printer.displayLn("  Field Changed  : ${sampleAuditLog.changedField ?: "Not Available"}")
            printer.displayLn("    Old Value      : ${sampleAuditLog.oldValue ?: "Not Available"}")
            printer.displayLn("    New Value      : ${sampleAuditLog.newValue ?: "Not Available"}")
            printer.displayLn("-".repeat(50))
            printer.displayLn("=== End of Audit Logs ===")
        }
    }

    @Test
    fun `should display correct action type for CREATE logs`() = runBlocking {
        // Given
        val createLog = sampleAuditLog.copy(actionType = AuditLogAction.CREATE)
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(createLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.getAllAuditLogs()

        // Then
        verify { printer.displayLn("----- [Audit] Created: ${createLog.itemName} -----") }
    }

    @Test
    fun `should display correct action type for UPDATE logs`() = runBlocking {
        // Given
        val updateLog = sampleAuditLog.copy(actionType = AuditLogAction.UPDATE)
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(updateLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.getAllAuditLogs()

        // Then
        verify { printer.displayLn("----- [Audit] Updated: ${updateLog.itemName} -----") }
    }

    @Test
    fun `should display correct action type for DELETE logs`() = runBlocking {
        // Given
        val deleteLog = sampleAuditLog.copy(actionType = AuditLogAction.DELETE)
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(deleteLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.getAllAuditLogs()

        // Then
        verify { printer.displayLn("----- [Audit] Deleted: ${deleteLog.itemName} -----") }
    }

    @Test
    fun `should call use case exactly once when getting logs`() = runBlocking {
        // Given
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(sampleAuditLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.getAllAuditLogs()

        // Then
        coVerify(exactly = 1) { getAuditLogsUseCase.getAllAuditLogs() }
    }

    // Edge cases for log content
    @Test
    fun `should handle empty item name gracefully`() = runBlocking {
        // Given
        val emptyNameLog = sampleAuditLog.copy(itemName = "")
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(emptyNameLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.getAllAuditLogs()

        // Then
        verify { printer.displayLn("----- [Audit] Created:  -----") }
    }



    @Test
    fun `should display Not Available when changedField is null`() = runBlocking {
        // Given
        val nullFieldLog = sampleAuditLog.copy(changedField = null)
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(nullFieldLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.getAllAuditLogs()

        // Then
        verify { printer.displayLn("  Field Changed  : Not Available") }
    }

    @Test
    fun `should display Not Available when oldValue is null`() = runBlocking {
        // Given
        val nullOldValueLog = sampleAuditLog.copy(oldValue = null)
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(nullOldValueLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.getAllAuditLogs()

        // Then
        verify { printer.displayLn("    Old Value      : Not Available") }
    }

    @Test
    fun `should display Not Available when newValue is null`() = runBlocking {
        // Given
        val nullNewValueLog = sampleAuditLog.copy(newValue = null)
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(nullNewValueLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.getAllAuditLogs()

        // Then
        verify { printer.displayLn("    New Value      : Not Available") }
    }

    @Test
    fun `should display all fields when all optional fields are null`() = runBlocking {
        // Given
        val minimalLog = sampleAuditLog.copy(
            changedField = null,
            oldValue = null,
            newValue = null
        )
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(minimalLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.getAllAuditLogs()

        // Then
        verify { printer.displayLn("  Field Changed  : Not Available") }
        verify { printer.displayLn("    Old Value      : Not Available") }
        verify { printer.displayLn("    New Value      : Not Available") }
    }

    // Empty state cases
    @Test
    fun `should show not found message when no logs exist`() = runBlocking {
        // Given
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns emptyList()
        every { printer.displayLn(any()) } just Runs

        // When
        cli.getAllAuditLogs()

        // Then
        verify { printer.displayLn("No audit logs found for any project or task.") }
    }

    // Time formatting tests
    @Test
    fun `should format AM time correctly`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 9, 15)

        // When
        val result = cli.formatTime(time)

        // Then
        assertEquals("9:15 AM", result)
    }

    @Test
    fun `should format PM time correctly`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 15, 45)

        // When
        val result = cli.formatTime(time)

        // Then
        assertEquals("3:45 PM", result)
    }

    @Test
    fun `should format midnight as 12-00 AM`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 0, 0)

        // When
        val result = cli.formatTime(time)

        // Then
        assertEquals("12:00 AM", result)
    }

    @Test
    fun `should format noon as 12-00 PM`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 12, 0)

        // When
        val result = cli.formatTime(time)

        // Then
        assertEquals("12:00 PM", result)
    }

    @Test
    fun `should pad single digit minutes with zero`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 13, 5)

        // When
        val result = cli.formatTime(time)

        // Then
        assertEquals("1:05 PM", result)
    }

    @Test
    fun `should handle 1 AM correctly`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 1, 0)

        // When
        val result = cli.formatTime(time)

        // Then
        assertEquals("1:00 AM", result)
    }

    @Test
    fun `should handle 11 PM correctly`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 23, 0)

        // When
        val result = cli.formatTime(time)

        // Then
        assertEquals("11:00 PM", result)
    }

    // Verification of printer calls
    @Test
    fun `should call printer for each log line`() = runBlocking {
        // Given
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(sampleAuditLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.getAllAuditLogs()

        // Then
        verify(exactly = 10) { printer.displayLn(any()) }
    }

    @Test
    fun `should print separator line between logs`() = runBlocking {
        // Given
        val log1 = sampleAuditLog.copy(auditId = UUID.randomUUID())
        val log2 = sampleAuditLog.copy(auditId = UUID.randomUUID())
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(log1, log2)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.getAllAuditLogs()

        // Then
        verify(exactly = 2) { printer.displayLn("-".repeat(50)) }
    }

    @Test
    fun `should print header and footer exactly once`() = runBlocking {
        // Given
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(sampleAuditLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.getAllAuditLogs()

        // Then
        verify(exactly = 1) { printer.displayLn("=== Audit Logs Overview ===") }
        verify(exactly = 1) { printer.displayLn("=== End of Audit Logs ===") }
    }
}