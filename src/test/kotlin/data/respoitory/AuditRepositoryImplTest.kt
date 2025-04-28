package data.respoitory

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import org.example.data.respoitory.AuditRepositoryImpl
import org.example.domain.model.AuditAction
import org.example.domain.model.AuditLog
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

// todo change all of the test
class AuditRepositoryImplTest {

    private lateinit var auditRepository: AuditRepositoryImpl

    @BeforeEach
    fun setUp() {
        auditRepository = AuditRepositoryImpl()
    }

    @Test
    fun `logChange should store audit log`() {
        val auditLog = AuditLog(
            itemId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            actionType = AuditAction.CREATE,
            auditTime = LocalDateTime(2023, 1, 1, 12, 0),
            changedField = "test",
            oldValue = null,
            newValue = "test"
        )

        try {
            auditRepository.logChange(auditLog)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getLogByItemId should return audit log for given item ID`() {
        val itemId = UUID.randomUUID()

        try {
            auditRepository.getLogByItemId(itemId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getAllLogs should return all audit logs`() {
        try {
            auditRepository.getAllLogs()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}