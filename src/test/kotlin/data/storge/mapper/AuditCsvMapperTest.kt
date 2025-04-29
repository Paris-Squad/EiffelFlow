package data.storge.mapper

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import org.example.data.storge.mapper.AuditCsvMapper
import org.example.domain.model.entities.AuditAction
import org.example.domain.model.entities.AuditLog
import java.util.UUID
import kotlin.test.Test

class AuditCsvMapperTest {

    private val auditCsvMapper = AuditCsvMapper()

    @Test
    fun `test mapFrom CSV to AuditLog`() {
        val csv =
            "123e4567-e89b-12d3-a456-426614174000,123e4567-e89b-12d3-a456-426614174001,Task,123e4567-e89b-12d3-a456-426614174002,User1,CREATE,2024-01-01T10:00:00,Title,null,NewTitle"

        try {
            val auditLog = auditCsvMapper.mapFrom(csv)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `test mapTo AuditLog to CSV`() {
        val auditLog = AuditLog(
            auditId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
            itemId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001"),
            itemName = "Task",
            userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002"),
            userName = "User1",
            actionType = AuditAction.CREATE,
            auditTime = LocalDateTime.parse("1999-08-07T22:22:22"),
            changedField = "Title",
            oldValue = null,
            newValue = "NewTitle"
        )

        try {
            val csv = auditCsvMapper.mapTo(auditLog)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}
