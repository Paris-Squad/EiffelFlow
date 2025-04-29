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
    fun `should map CSV line to AuditLog entity correctly`() {
        //Given / When
        val result = auditCsvMapper.mapFrom(CSV_STRING_LINE)

        //Then
        assertThat(result).isEqualTo(AUDIT_LOG)
    }

    @Test
    fun `should map AuditLog entity to CSV line correctly`() {
        //Given / When
        val result = auditCsvMapper.mapTo(AUDIT_LOG)

        //Then
        assertThat(result).isEqualTo(CSV_STRING_LINE)
    }

    companion object {
        private val AUDIT_LOG = AuditLog(
            auditId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748"),
            itemId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5740"),
            itemName = "Task",
            userId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5741"),
            userName = "User1",
            actionType = AuditAction.CREATE,
            auditTime = LocalDateTime.parse("1999-08-07T22:22:22"),
            changedField = "Title",
            oldValue = null,
            newValue = "NewTitle"
        )

        private const val CSV_STRING_LINE =
            "02ad4499-5d4c-4450-8fd1-8294f1bb5748,02ad4499-5d4c-4450-8fd1-8294f1bb5740,Task,02ad4499-5d4c-4450-8fd1-8294f1bb5741,User1,CREATE,1999-08-07T22:22:22,Title,,NewTitle"
    }
}
