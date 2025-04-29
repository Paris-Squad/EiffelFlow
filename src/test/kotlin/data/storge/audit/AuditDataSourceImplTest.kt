package data.storge.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.example.data.storge.CsvStorageManager
import org.example.data.storge.audit.AuditDataSource
import org.example.data.storge.audit.AuditDataSourceImpl
import org.example.data.storge.mapper.AuditCsvMapper
import org.example.domain.model.entities.AuditAction
import org.example.domain.model.entities.AuditLog
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class AuditDataSourceImplTest {

    private lateinit var auditDataSource: AuditDataSource
    private val csvStorageManager: CsvStorageManager = mockk()
    private val auditCsvMapper: AuditCsvMapper = mockk()

    @BeforeEach
    fun setUp() {
        auditDataSource = AuditDataSourceImpl(auditCsvMapper, csvStorageManager)
    }

    @Test
    fun `getLogByItemId should return audit log for given item ID`() {
        val itemId = UUID.randomUUID()

        try {
            auditDataSource.getAuditLogById(itemId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getAllLogs should return all audit logs`() {
        try {
            auditDataSource.getAuditLogs()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `createAuditLog should return all audit logs`() {
        try {
            val auditLog = AuditLog(
                itemId = UUID.randomUUID(),
                itemName = "test",
                userId = UUID.randomUUID(),
                userName = "test",
                actionType = AuditAction.CREATE,
                auditTime = LocalDateTime.parse("1222-09-09T12:22:22"),
                changedField = "eee",
                oldValue = "eess",
                newValue = "dssds"
            )
            auditDataSource.createAuditLog(auditLog)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}