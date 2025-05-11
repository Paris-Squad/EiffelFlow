package presentation.presenter.audit

import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.example.domain.usecase.audit.GetAllAuditLogsUseCase
import org.example.presentation.audit.GetAuditLogsCLI
import org.example.presentation.helper.extensions.toFormattedDateTime
import org.example.presentation.io.Printer
import org.junit.jupiter.api.Test
import utils.MockAuditLog
import java.util.UUID

class GetAuditLogsCLITest {
    private val getAuditLogsUseCase: GetAllAuditLogsUseCase = mockk()
    private val printer: Printer = mockk()
    private val cli = GetAuditLogsCLI(getAuditLogsUseCase, printer)

    private val sampleAuditLog = MockAuditLog.AUDIT_LOG.copy(
        itemName = "Test Task",
        auditTime = LocalDateTime(2023, 1, 1, 14, 30)
    )

    @Test
    fun `should show all log details when printing audit entries`() = runBlocking {
        // Given
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(sampleAuditLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.start()

        // Then
        verifySequence {
            printer.displayLn("=== Audit Logs Overview ===")
            printer.displayLn("Created 'Test Task'")
            printer.displayLn("  Audit ID        : ${sampleAuditLog.auditId}")
            printer.displayLn("  Date & Time     : ${sampleAuditLog.auditTime.toFormattedDateTime()}")
            printer.displayLn("  Modified By     : ${sampleAuditLog.editorName}")
            printer.displayLn("  Field Changed   : ${sampleAuditLog.changedField ?: "Not Available"}")
            printer.displayLn("  Old Value       : ${sampleAuditLog.oldValue ?: "Not Available"}")
            printer.displayLn("  New Value       : ${sampleAuditLog.newValue ?: "Not Available"}")
            printer.displayLn("-".repeat(50))
            printer.displayLn("=== End of Audit Logs ===")
        }
    }

    @Test
    fun `should call use case exactly once when getting logs`() = runBlocking {
        // Given
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(sampleAuditLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.start()

        // Then
        coVerify(exactly = 1) { getAuditLogsUseCase.getAllAuditLogs() }
    }

    // Empty state cases
    @Test
    fun `should show not found message when no logs exist`() = runBlocking {
        // Given
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns emptyList()
        every { printer.displayLn(any()) } just Runs

        // When
        cli.start()

        // Then
        verify { printer.displayLn("No audit logs found for any project or task.") }
    }

    @Test
    fun `should handle empty item name gracefully`() = runBlocking {
        // Given
        val emptyNameLog = sampleAuditLog.copy(itemName = "")
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(emptyNameLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.start()

        // Then
        verify { printer.displayLn("Created ''") }
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
        cli.start()

        // Then
        verify { printer.displayLn("  Field Changed   : Not Available") }
        verify { printer.displayLn("  Old Value       : Not Available") }
        verify { printer.displayLn("  New Value       : Not Available") }
    }

    // Verification of printer calls
    @Test
    fun `should call printer for each log line`() = runBlocking {
        // Given
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(sampleAuditLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.start()

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
        cli.start()

        // Then
        verify(exactly = 2) { printer.displayLn("-".repeat(50)) }
    }

    @Test
    fun `should print header and footer exactly once`() = runBlocking {
        // Given
        coEvery { getAuditLogsUseCase.getAllAuditLogs() } returns listOf(sampleAuditLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.start()

        // Then
        verify(exactly = 1) { printer.displayLn("=== Audit Logs Overview ===") }
        verify(exactly = 1) { printer.displayLn("=== End of Audit Logs ===") }
    }
}