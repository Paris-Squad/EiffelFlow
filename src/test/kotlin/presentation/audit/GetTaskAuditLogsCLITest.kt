package presentation.audit

import io.mockk.*
import kotlinx.datetime.LocalDateTime
import org.example.domain.usecase.audit.GetTaskAuditUseCase
import org.example.presentation.audit.GetTaskAuditLogsCLI
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import utils.MockAuditLog
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTaskAuditLogsCLITest {
    private val getTaskAuditLogsUseCase: GetTaskAuditUseCase = mockk()
    private val inputReader: InputReader = mockk()
    private val printer: Printer = mockk()
    private val cli = GetTaskAuditLogsCLI(getTaskAuditLogsUseCase,inputReader,printer)

    private val validTaskId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
    private val sampleAuditLog = MockAuditLog.AUDIT_LOG.copy(
        itemId = validTaskId,
        itemName = "Test Task",
        auditTime = LocalDateTime(2023, 1, 1, 14, 30)
    )
    // success
    @Test
    fun `should display task audit logs for valid task ID`(){
        // Given
        every { inputReader.readString() } returns validTaskId.toString()
        every { printer.displayLn(any()) } just Runs
        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns listOf(sampleAuditLog)
        // When
        cli.getTaskAuditLogsInput()
        // Then
        verify { printer.displayLn("Enter Task ID to retrieve audit logs:") }
        verify { printer.displayLn("[Task] Created Test Task") }
        verify { printer.displayLn("  Date         : 2023-01-01 / Time: 2:30 PM") }
    }

//    @Test
//    fun `should show all log details when printing audit entries`() = runBlocking {
//        // Given
//        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns listOf(sampleAuditLog)
//        every { printer.displayLn(any()) } just Runs
//        // When
//        cli.displayAuditLogsForTask(validTaskId)
//        // Then
//        verify {
//            printer.displayLn("[Task] Created Test Task")
//            printer.displayLn("  Audit ID     : ${sampleAuditLog.auditId}")
//            printer.displayLn("  Date         : 2023-01-01 / Time: 2:30 PM")
//            printer.displayLn("  Modified By  : ${sampleAuditLog.editorName}")
//            printer.displayLn("  Field Changed: ${sampleAuditLog.changedField ?: "Not Available"}")
//            printer.displayLn("    Old        : ${sampleAuditLog.oldValue ?: "Not Available"}")
//            printer.displayLn("    New        : ${sampleAuditLog.newValue ?: "Not Available"}")
//            printer.displayLn("-".repeat(50))
//        }
//    }

//    @Test
//    fun `should label log as Task when itemId is different from projectId`() = runBlocking {
//        // Given
//        val differentLog = sampleAuditLog.copy(itemId = UUID.randomUUID())
//        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns listOf(differentLog)
//        every { printer.displayLn(any()) } just Runs
//        // When
//        cli.displayAuditLogsForTask(validTaskId)
//        // Then
//        verify { printer.displayLn("[Task] Created ${differentLog.itemName}") }
//    }

//    @Test
//    fun `should display Updated when log is UPDATE`() = runBlocking {
//        // Given
//        val updateLog = sampleAuditLog.copy(actionType = AuditLogAction.UPDATE)
//
//        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns listOf(updateLog)
//        every { printer.displayLn(any()) } just Runs
//        // When
//        cli.displayAuditLogsForTask(validTaskId)
//        // Then
//        verify { printer.displayLn("[Task] Updated ${updateLog.itemName}") }
//    }

//    @Test
//    fun `should display Deleted when log is DELETE`() = runBlocking {
//        // Given
//        val deleteLog = sampleAuditLog.copy(actionType = AuditLogAction.DELETE)
//        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns listOf(deleteLog)
//        every { printer.displayLn(any()) } just Runs
//        // When
//        cli.displayAuditLogsForTask(validTaskId)
//        // Then
//        verify { printer.displayLn("[Task] Deleted ${deleteLog.itemName}") }
//    }

//    @Test
//    fun `should get all log details when printing audit entries`() = runBlocking {
//        // Given
//        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns listOf(sampleAuditLog)
//        every { printer.displayLn(any()) } just Runs
//        // When
//        cli.displayAuditLogsForTask(validTaskId)
//        // Then
//        coVerify { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) }
//
//        verify { printer.displayLn("  Audit ID     : ${sampleAuditLog.auditId}") }
//        verify { printer.displayLn("  Date         : ${sampleAuditLog.auditTime.date} / Time: ${cli.formatTime(sampleAuditLog.auditTime)}") }
//        verify { printer.displayLn("  Modified By  : ${sampleAuditLog.editorName}") }
//        verify { printer.displayLn("  Field Changed: ${sampleAuditLog.changedField}") }
//        verify { printer.displayLn("    Old        : ${sampleAuditLog.oldValue}") }
//        verify { printer.displayLn("    New        : ${sampleAuditLog.newValue}") }
//    }


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

//    @Test
//    fun `should show error not found message when Task has no logs`() = runBlocking {
//        // Given
//        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns emptyList()
//        every { printer.displayLn(any()) } just Runs
//        // When
//        cli.displayAuditLogsForTask(validTaskId)
//        // Then
//        verify { printer.displayLn("No audit logs found for Task ID: $validTaskId.") }
//    }

//    @Test
//    fun `should show Not Available when changedField, oldValue and newValue are null`() = runBlocking {
//        // Given
//        val logWithNulls = sampleAuditLog.copy(
//            changedField = null,
//            oldValue = null,
//            newValue = null
//        )
//
//        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns listOf(logWithNulls)
//        every { printer.displayLn(any()) } just Runs
//        // When
//        cli.displayAuditLogsForTask(validTaskId)
//        // Then
//        verify { printer.displayLn("  Field Changed: Not Available") }
//        verify { printer.displayLn("    Old        : Not Available") }
//        verify { printer.displayLn("    New        : Not Available") }
//    }

    @Test
    fun `should show error invalid UUID message when Task ID is empty or null or blank`() {
        listOf(null, "", "   ").forEach {
            // Given
            every { inputReader.readString() } returns it
            every { printer.displayLn(any()) } just Runs
            // When
            cli.getTaskAuditLogsInput()
            // Then
            verify { printer.displayLn("Task ID cannot be empty. Please provide a valid UUID.") }
            clearMocks(printer)
        }
    }
//    @Test
//    fun `should handle empty audit logs gracefully`() = runBlocking {
//        // Given
//        coEvery { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) } returns emptyList()
//        every { printer.displayLn(any()) } just Runs
//        // When
//        cli.displayAuditLogsForTask(validTaskId)
//        // Then
//        coVerify { getTaskAuditLogsUseCase.getTaskAuditLogsById(validTaskId) }
//        verify { printer.displayLn("No audit logs found for Task ID: $validTaskId.") }
//    }
    // helper function
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

}
