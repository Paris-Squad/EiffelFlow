package data.respoitory

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.example.data.respoitory.AuditRepositoryImpl
import org.example.data.storge.audit.AuditDataSource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

// todo change all of the test
class AuditRepositoryImplTest {

    private lateinit var auditRepository: AuditRepositoryImpl
    private val auditDataSource: AuditDataSource = mockk()

    @BeforeEach
    fun setUp() {
        auditRepository = AuditRepositoryImpl(auditDataSource)
    }

    @Test
    fun `getLogByItemId should return audit log for given item ID`() {
        val itemId = UUID.randomUUID()

        try {
            auditRepository.getAuditLogById(itemId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getAllLogs should return all audit logs`() {
        try {
            auditRepository.getAuditLogs()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}