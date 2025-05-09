package presentation.presenter.audit

import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.AuditLogAction
import org.example.domain.usecase.audit.GetProjectAuditUseCase
import org.example.presentation.presenter.audit.GetProjectAuditLogsCLI
import org.example.presentation.presenter.io.InputReader
import org.example.presentation.presenter.io.Printer
import org.junit.jupiter.api.Test
import utils.MockAuditLog
import java.util.UUID
import kotlin.test.assertEquals

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
    fun `should display audit logs for valid project ID`() = runBlocking {
        // Given
        every { inputReader.readString() } returns validProjectId.toString()
        coEvery { getProjectAuditUseCase.getProjectAuditLogsById(validProjectId) } returns listOf(sampleAuditLog)
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getProjectAuditLogsInput()
        // Then
        verify { printer.displayLn("Enter project ID to get Audit Logs: ") }
        verify { printer.displayLn("Audit Logs for Project: Test Project") }
        verify { printer.displayLn("[Project] Created Test Project") }
        verify { printer.displayLn("  Date: 2023-01-01T14:30 / Time: 2:30 PM") }
    }

    @Test
    fun `should show error when project ID has invalid format`() {
        // Given
        every { inputReader.readString() } returns "invalid-uuid"
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getProjectAuditLogsInput()
        // Then
        verify { printer.displayLn("Invalid Project ID format.") }
    }

    @Test
    fun `should show not found message when project has no logs`() = runBlocking {
        // Given
        coEvery { getProjectAuditUseCase.getProjectAuditLogsById(validProjectId) } returns emptyList()
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getProjectAuditLogs(validProjectId)
        // Then
        verify { printer.displayLn("No audit logs found for project $validProjectId.") }
    }

    @Test
    fun `should show error message when file operation fails`() = runBlocking {
        // Given
        coEvery { getProjectAuditUseCase.getProjectAuditLogsById(validProjectId) } throws
                EiffelFlowException.NotFoundException("file connection failed")
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getProjectAuditLogs(validProjectId)
        // Then
        verify { printer.displayLn("Failed to get audit logs: file connection failed") }
    }

    @Test
    fun `should return formatted time in 12-hour format when given 24-hour time`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 14, 30)
        // When
        val result = cli.formatTime(time)
        // Then
        assertEquals("2:30 PM", result)
    }

    @Test
    fun `should return 12-00 AM when given midnight (00-00)`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 0, 0)
        // When
        val result = cli.formatTime(time)
        // Then
        assertEquals("12:00 AM", result)
    }

    @Test
    fun `should return 12-00 PM when given noon (12-00)`() {
        // Given
        val time = LocalDateTime(2023, 1, 1, 12, 0)
        // When
        val result = cli.formatTime(time)
        // Then
        assertEquals("12:00 PM", result)
    }

    @Test
    fun `should show all log details when printing audit entries`() = runBlocking {
        // Given
        coEvery { getProjectAuditUseCase.getProjectAuditLogsById(validProjectId) } returns listOf(sampleAuditLog)
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getProjectAuditLogs(validProjectId)
        // Then
        verify {
            printer.displayLn("Audit Logs for Project: Test Project")
            printer.displayLn("[Project] Created Test Project")
            printer.displayLn("  Audit ID: ${sampleAuditLog.auditId}")
            printer.displayLn("  Date: 2023-01-01T14:30 / Time: 2:30 PM")
            printer.displayLn("  Modified By: ${sampleAuditLog.editorName}")
            printer.displayLn("  Field Changed: ${sampleAuditLog.changedField ?: "Not Available"}")
            printer.displayLn("    Old: ${sampleAuditLog.oldValue ?: "Not Available"}")
            printer.displayLn("    New: ${sampleAuditLog.newValue ?: "Not Available"}")
            printer.displayLn("_".repeat(50))
        }
    }

    @Test
    fun `should handle general exception in input method`() {
        // Given
        every { inputReader.readString() } throws RuntimeException("General error")
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getProjectAuditLogsInput()
        // Then
        verify { printer.displayLn("An error occurred while retrieving audit logs: General error") }
    }

    @Test
    fun `should handle empty item name in audit logs`() = runBlocking {
        // Given
        val logWithEmptyName = sampleAuditLog.copy(itemName = "")
        coEvery { getProjectAuditUseCase.getProjectAuditLogsById(validProjectId) } returns listOf(logWithEmptyName)
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getProjectAuditLogs(validProjectId)
        // Then
        verify { printer.displayLn("Audit Logs for Project: Unnamed Project") }
        verify { printer.displayLn("[Project] Created ") }
    }

    @Test
    fun `should label log as Task when itemId is different from projectId`() = runBlocking {
        // Given
        val differentLog = sampleAuditLog.copy(itemId = UUID.randomUUID())
        coEvery { getProjectAuditUseCase.getProjectAuditLogsById(validProjectId) } returns listOf(differentLog)
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getProjectAuditLogs(validProjectId)
        // Then
        verify { printer.displayLn("[Task] Created ${differentLog.itemName}") }
    }

    @Test
    fun `should display Updated when log is UPDATE`() = runBlocking {
        // Given
        val updateLog = sampleAuditLog.copy(actionType = AuditLogAction.UPDATE)

        coEvery { getProjectAuditUseCase.getProjectAuditLogsById(validProjectId) } returns listOf(updateLog)
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getProjectAuditLogs(validProjectId)
        // Then
        verify { printer.displayLn("[Project] Updated ${updateLog.itemName}") }
    }

    @Test
    fun `should display Deleted when log is DELETE`() = runBlocking {
        // Given
        val deleteLog = sampleAuditLog.copy(actionType = AuditLogAction.DELETE)
        coEvery { getProjectAuditUseCase.getProjectAuditLogsById(validProjectId) } returns listOf(deleteLog)
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getProjectAuditLogs(validProjectId)
        // Then
        verify { printer.displayLn("[Project] Deleted ${deleteLog.itemName}") }
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
        cli.getProjectAuditLogs(validProjectId)
        // Then
        verify { printer.displayLn("  Field Changed: Not Available") }
        verify { printer.displayLn("    Old: Not Available") }
        verify { printer.displayLn("    New: Not Available") }
    }

    @Test
    fun `should call getProjectAuditLogs when input is valid UUID`() = runBlocking {
        // Given
        val cli = spyk(GetProjectAuditLogsCLI(getProjectAuditUseCase, inputReader, printer))
        val validId = UUID.randomUUID()
        every { inputReader.readString() } returns validId.toString()
        coEvery { cli.getProjectAuditLogs(validId) } just Runs
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getProjectAuditLogsInput()
        // Then
        verify { cli.getProjectAuditLogs(validId) }
    }
    @Test
    fun `should catch exception thrown inside getProjectAuditLogs`() {
        // Given
        val validId = UUID.randomUUID()
        every { inputReader.readString() } returns validId.toString()
        every { printer.displayLn(any()) } just Runs
        val cliSpy = spyk(cli)
        coEvery { cliSpy.getProjectAuditLogs(validId) } throws RuntimeException("Unexpected error")
        // When
        cliSpy.getProjectAuditLogsInput()
        // Then
        verify { printer.displayLn("An error occurred while retrieving audit logs: Unexpected error") }
    }
    @Test
    fun `should print error when input is blank`() {
        // Given
        every { inputReader.readString() } returns "   "
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getProjectAuditLogsInput()
        // Then
        verify { printer.displayLn("Project ID cannot be empty.") }
    }
    @Test
    fun `should print error when input is not a valid UUID`() {
        // Given
        every { inputReader.readString() } returns "invalid-uuid"
        every { printer.displayLn(any()) } just Runs
        // When
        cli.getProjectAuditLogsInput()
        // Then
        verify { printer.displayLn("Invalid Project ID format.") }
    }
    @Test
    fun `should handle EiffelFlowException when getting audit logs`() {
        // Given
        val id = UUID.randomUUID().toString()
        every { inputReader.readString() } returns id
        every { printer.displayLn(any()) } just Runs
        val cliSpy = spyk(cli)
        coEvery { cliSpy.getProjectAuditLogs(UUID.fromString(id)) } throws EiffelFlowException.NotFoundException("Audit failed")
        // When
        cliSpy.getProjectAuditLogsInput()
        // Then
        verify { printer.displayLn("Failed to get audit logs: Audit failed") }
    }
    @Test
    fun `should handle generic exception when getting audit logs`() {
        // Given
        val id = UUID.randomUUID().toString()
        every { inputReader.readString() } returns id
        every { printer.displayLn(any()) } just Runs
        val cliSpy = spyk(cli)
        coEvery { cliSpy.getProjectAuditLogs(UUID.fromString(id)) } throws RuntimeException("Unexpected")
        // When
        cliSpy.getProjectAuditLogsInput()
        // Then
        verify { printer.displayLn("An error occurred while retrieving audit logs: Unexpected") }
    }

    @Test
    fun `should show error when project ID is empty or null or blank`() {

        listOf(null, "", "   ").forEach {
            every { inputReader.readString() } returns it
            every { printer.displayLn(any()) } just Runs
            cli.getProjectAuditLogsInput()
            verify { printer.displayLn("Project ID cannot be empty.") }
            clearMocks(printer)
        }
    }

}
