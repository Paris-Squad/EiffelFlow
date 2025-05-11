package presentation.presenter.audit

import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.example.domain.model.AuditLogAction
import org.example.domain.usecase.audit.GetProjectAuditUseCase
import org.example.presentation.audit.GetProjectAuditLogsCLI
import org.example.presentation.helper.extensions.toFormattedDateTime
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.junit.jupiter.api.Test
import utils.MockAuditLog
import java.util.UUID

class GetProjectAuditLogsCLITest {

    private val getProjectAuditUseCase: GetProjectAuditUseCase = mockk()
    private val inputReader: InputReader = mockk()
    private val printer: Printer = mockk()
    private val cli = GetProjectAuditLogsCLI(getProjectAuditUseCase, inputReader, printer)

    private val validProjectId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
    private val sampleAuditLog = MockAuditLog.AUDIT_LOG.copy(
        itemId = validProjectId,
        itemName = "Test Project",
        auditTime = LocalDateTime(2023, 1, 1, 14, 30)
    )

    @Test
    fun `should display audit logs when get valid project ID`() = runBlocking {
        // Given
        every { inputReader.readString() } returns validProjectId.toString()
        coEvery { getProjectAuditUseCase.getProjectAuditLogsById(validProjectId) } returns listOf(sampleAuditLog)
        every { printer.displayLn(any()) } just Runs
        // When
        cli.start()
        // Then
        verify { printer.displayLn("Please enter the Project ID to retrieve the Audit Logs:") }
        verify { printer.displayLn("Audit Logs for Project: '${sampleAuditLog.itemName}'") }
        verify { printer.displayLn("[Project]  Created '${sampleAuditLog.itemName}'") }
        verify { printer.displayLn("  Date & Time     : ${sampleAuditLog.auditTime.toFormattedDateTime()}") }
    }

    @Test
    fun `should show not found message when project has no logs`() = runBlocking {
        // Given
        coEvery { getProjectAuditUseCase.getProjectAuditLogsById(validProjectId) } returns emptyList()
        every { printer.displayLn(any()) } just Runs
        // When
        cli.showProjectAuditLogs(validProjectId)
        // Then
        verify { printer.displayLn("No audit logs found for the specified Project ID: $validProjectId.") }
    }

    @Test
    fun `should show all log details when printing audit entries`() = runBlocking {
        // Given
        coEvery { getProjectAuditUseCase.getProjectAuditLogsById(validProjectId) } returns listOf(sampleAuditLog)
        every { printer.displayLn(any()) } just Runs
        // When
        cli.showProjectAuditLogs(validProjectId)
        // Then
        verify {
            printer.displayLn("Audit Logs for Project: '${sampleAuditLog.itemName}'")
            printer.displayLn("[Project]  Created '${sampleAuditLog.itemName}'")
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
    fun `should handle empty item name in audit logs`() = runBlocking {
        // Given
        val logWithEmptyName = sampleAuditLog.copy(itemName = "", actionType = AuditLogAction.CREATE)
        coEvery { getProjectAuditUseCase.getProjectAuditLogsById(validProjectId) } returns listOf(logWithEmptyName)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.showProjectAuditLogs(validProjectId)

        // Then
        verify { printer.displayLn("Audit Logs for Project: 'Unnamed Project'") }
        verify { printer.displayLn("[Project]  Created ''") }
    }



    @Test
    fun `should label log as Task when itemId is different from projectId`() = runBlocking {
        // Given
        val differentLog = sampleAuditLog.copy(itemId = UUID.randomUUID())
        coEvery { getProjectAuditUseCase.getProjectAuditLogsById(validProjectId) } returns listOf(differentLog)
        every { printer.displayLn(any()) } just Runs

        // When
        cli.showProjectAuditLogs(validProjectId)

        // Then
        verify { printer.displayLn("[Task]     Created '${differentLog.itemName}'") }
    }

    @Test
    fun `should show Not Available when changedField, oldValue and newValue are null`() = runBlocking {
        // Given
        val logWithNulls = sampleAuditLog.copy(
            changedField = null,
            oldValue = null,
            newValue = null
        )

        coEvery { getProjectAuditUseCase.getProjectAuditLogsById(validProjectId) } returns listOf(logWithNulls)
        every { printer.displayLn(any()) } just Runs
        // When
        cli.showProjectAuditLogs(validProjectId)
        // Then
        verify { printer.displayLn("  Field Changed   : Not Available") }
        verify { printer.displayLn("  Old             : Not Available") }
        verify { printer.displayLn("  New             : Not Available") }
    }

    @Test
    fun `should call getProjectAuditLogs when input is valid UUID`() = runBlocking {
        // Given
        val cli = spyk(GetProjectAuditLogsCLI(getProjectAuditUseCase, inputReader, printer))
        val validId = UUID.randomUUID()
        every { inputReader.readString() } returns validId.toString()
        coEvery { cli.showProjectAuditLogs(validId) } just Runs
        every { printer.displayLn(any()) } just Runs
        // When
        cli.start()
        // Then
        verify { cli.showProjectAuditLogs(validId) }
    }

    @Test
    fun `should print error message when project ID is empty or null or blank`() {

        listOf(null, "", "   ").forEach {
            // Given
            every { inputReader.readString() } returns it
            every { printer.displayLn(any()) } just Runs
            // When
            cli.start()
            // Then
            verify { printer.displayLn("Project ID cannot be left blank. Please provide a valid ID.") }
            clearMocks(printer)
        }
    }
}
