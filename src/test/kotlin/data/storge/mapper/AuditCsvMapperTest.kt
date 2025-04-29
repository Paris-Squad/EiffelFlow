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
    fun `should map full CSV line to full AuditLog entity correctly`() {
        //Given / When
        val result = auditCsvMapper.mapFrom(FULL_CSV_STRING_LINE)

        //Then
        assertThat(result).isEqualTo(AUDIT_LOG)
    }

    @Test
    fun `should map full AuditLog entity to CSV line correctly`() {
        //Given / When
        val result = auditCsvMapper.mapTo(AUDIT_LOG)

        //Then
        assertThat(result).isEqualTo(FULL_CSV_STRING_LINE)
    }

    @Test
    fun `should map full CSV line to full AuditLog entity correctly with newValue = null`() {
        //Given / When
        val result = auditCsvMapper.mapFrom(CSV_STRING_LINE_WITH_NEW_VALUE_NULL)

        //Then
        assertThat(result).isEqualTo(AUDIT_LOG.copy(newValue = null))
    }

    @Test
    fun `should map full AuditLog entity to CSV line correctly with newValue = null`() {
        //Given / When
        val result = auditCsvMapper.mapTo(AUDIT_LOG.copy(newValue = null))

        //Then
        assertThat(result).isEqualTo(CSV_STRING_LINE_WITH_NEW_VALUE_NULL)
    }

    @Test
    fun `should map full CSV line to full AuditLog entity correctly with oldValue = null`() {
        //Given / When
        val result = auditCsvMapper.mapFrom(CSV_STRING_LINE_WITH_OLD_VALUE_NULL)

        //Then
        assertThat(result).isEqualTo(AUDIT_LOG.copy(oldValue = null))
    }

    @Test
    fun `should map full AuditLog entity to CSV line correctly with oldValue = null`() {
        //Given / When
        val result = auditCsvMapper.mapTo(AUDIT_LOG.copy(oldValue = null))

        //Then
        assertThat(result).isEqualTo(CSV_STRING_LINE_WITH_OLD_VALUE_NULL)
    }

    @Test
    fun `should map full CSV line to full AuditLog entity correctly with changedField = null`() {
        //Given / When
        val result = auditCsvMapper.mapFrom(CSV_STRING_LINE_WITH_CHANGED_FIELD_NULL)

        //Then
        assertThat(result).isEqualTo(AUDIT_LOG.copy(changedField = null))
    }

    @Test
    fun `should map full AuditLog entity to CSV line correctly with changedField = null`() {
        //Given / When
        val result = auditCsvMapper.mapTo(AUDIT_LOG.copy(changedField = null))

        //Then
        assertThat(result).isEqualTo(CSV_STRING_LINE_WITH_CHANGED_FIELD_NULL)
    }

    @Test
    fun `should map full CSV line to full AuditLog entity correctly with changedField & oldValue & newValue = null`() {
        //Given / When
        val result = auditCsvMapper.mapFrom(CSV_STRING_LINE_MISSING_CHANGED_FIELD_AND_OLD_VALUE_AND_NEW_VALUE)

        //Then
        assertThat(result).isEqualTo(AUDIT_LOG.copy(changedField = null, oldValue = null, newValue = null))
    }

    @Test
    fun `should map full AuditLog entity to CSV line correctly with changedField & oldValue & newValue = null`() {
        //Given / When
        val result = auditCsvMapper.mapTo(AUDIT_LOG.copy(changedField = null, oldValue = null, newValue = null))

        //Then
        assertThat(result).isEqualTo(CSV_STRING_LINE_MISSING_CHANGED_FIELD_AND_OLD_VALUE_AND_NEW_VALUE)
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
            oldValue = "OldTitle",
            newValue = "NewTitle"
        )

        private const val FULL_CSV_STRING_LINE =
            "02ad4499-5d4c-4450-8fd1-8294f1bb5748,02ad4499-5d4c-4450-8fd1-8294f1bb5740,Task,02ad4499-5d4c-4450-8fd1-8294f1bb5741,User1,CREATE,1999-08-07T22:22:22,Title,OldTitle,NewTitle"
        private const val CSV_STRING_LINE_WITH_NEW_VALUE_NULL =
            "02ad4499-5d4c-4450-8fd1-8294f1bb5748,02ad4499-5d4c-4450-8fd1-8294f1bb5740,Task,02ad4499-5d4c-4450-8fd1-8294f1bb5741,User1,CREATE,1999-08-07T22:22:22,Title,OldTitle,"
        private const val CSV_STRING_LINE_WITH_OLD_VALUE_NULL =
            "02ad4499-5d4c-4450-8fd1-8294f1bb5748,02ad4499-5d4c-4450-8fd1-8294f1bb5740,Task,02ad4499-5d4c-4450-8fd1-8294f1bb5741,User1,CREATE,1999-08-07T22:22:22,Title,,NewTitle"
        private const val CSV_STRING_LINE_WITH_CHANGED_FIELD_NULL =
            "02ad4499-5d4c-4450-8fd1-8294f1bb5748,02ad4499-5d4c-4450-8fd1-8294f1bb5740,Task,02ad4499-5d4c-4450-8fd1-8294f1bb5741,User1,CREATE,1999-08-07T22:22:22,,OldTitle,NewTitle"
        private const val CSV_STRING_LINE_MISSING_CHANGED_FIELD_AND_OLD_VALUE_AND_NEW_VALUE=
            "02ad4499-5d4c-4450-8fd1-8294f1bb5748,02ad4499-5d4c-4450-8fd1-8294f1bb5740,Task,02ad4499-5d4c-4450-8fd1-8294f1bb5741,User1,CREATE,1999-08-07T22:22:22,,,"
    }
}
