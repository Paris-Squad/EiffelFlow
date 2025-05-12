package presentation.presenter.audit

import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
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


    @Test
    fun `should display task audit logs for valid task ID`() = runBlocking {
        // Given
        every { inputReader.readString() } returns validTaskId.toString()
        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns listOf(sampleAuditLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.start()

        // Then
        verifySequence {
            printer.displayLn("Enter Task ID to retrieve audit logs:")
            printer.displayLn("=== Audit Logs on Task: $validTaskId ===")
            printer.displayLn("[Task] Created 'Test Task'")
            printer.displayLn("  Audit ID        : ${sampleAuditLog.auditId}")
            printer.displayLn("  Date & Time     : ${sampleAuditLog.auditTime.toFormattedDateTime()}")
            printer.displayLn("  Modified By     : ${sampleAuditLog.editorName}")
            printer.displayLn("  Field Changed   : ${sampleAuditLog.changedField ?: "Not Available"}")
            printer.displayLn("  Old             : ${sampleAuditLog.oldValue ?: "Not Available"}")
            printer.displayLn("  New             : ${sampleAuditLog.newValue ?: "Not Available"}")
            printer.displayLn("-".repeat(50))
            printer.displayLn("=== End of Audit Logs ===")
        }
    }


    @Test
    fun `should print error message when Task ID is empty or null or blank`() {

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
    fun `should show message when no audit logs found`() = runBlocking {
        // Given
        every { inputReader.readString() } returns validTaskId.toString()
        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns emptyList()
        every { printer.displayLn(any()) } just Runs

        // When
        cli.start()

        // Then
        verify {
            printer.displayLn("No audit logs found for Task ID: $validTaskId.")
        }
    }

    @Test
    fun `should display Not Available for null optional fields`() = runBlocking {
        // Given
        val logWithNulls = sampleAuditLog.copy(
            changedField = null,
            oldValue = null,
            newValue = null
        )
        every { inputReader.readString() } returns validTaskId.toString()
        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns listOf(logWithNulls)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.start()

        // Then
        verify {
            printer.displayLn("  Field Changed   : Not Available")
            printer.displayLn("  Old             : Not Available")
            printer.displayLn("  New             : Not Available")
        }
    }

}