package data.storage.mapper

import com.google.common.truth.Truth.assertThat
import org.example.data.storage.mapper.AuditCsvMapper
import utils.MockAuditLog
import kotlin.test.Test

class AuditCsvMapperTest {

    private val auditCsvMapper = AuditCsvMapper()

    @Test
    fun `should map full CSV line to full AuditLog entity correctly`() {
        //Given / When
        val result = auditCsvMapper.mapFrom(MockAuditLog.FULL_CSV_STRING_LINE)

        //Then
        assertThat(result).isEqualTo(MockAuditLog.AUDIT_LOG)
    }

    @Test
    fun `should map full AuditLog entity to CSV line correctly`() {
        //Given / When
        val result = auditCsvMapper.mapTo(MockAuditLog.AUDIT_LOG)

        //Then
        assertThat(result).isEqualTo(MockAuditLog.FULL_CSV_STRING_LINE)
    }

    @Test
    fun `should map full CSV line to full AuditLog entity correctly with newValue = null`() {
        //Given / When
        val result = auditCsvMapper.mapFrom(MockAuditLog.CSV_STRING_LINE_WITH_NEW_VALUE_NULL)

        //Then
        assertThat(result).isEqualTo(MockAuditLog.AUDIT_LOG.copy(newValue = null))
    }

    @Test
    fun `should map full AuditLog entity to CSV line correctly with newValue = null`() {
        //Given / When
        val result = auditCsvMapper.mapTo(MockAuditLog.AUDIT_LOG.copy(newValue = null))

        //Then
        assertThat(result).isEqualTo(MockAuditLog.CSV_STRING_LINE_WITH_NEW_VALUE_NULL)
    }

    @Test
    fun `should map full CSV line to full AuditLog entity correctly with oldValue = null`() {
        //Given / When
        val result = auditCsvMapper.mapFrom(MockAuditLog.CSV_STRING_LINE_WITH_OLD_VALUE_NULL)

        //Then
        assertThat(result).isEqualTo(MockAuditLog.AUDIT_LOG.copy(oldValue = null))
    }

    @Test
    fun `should map full AuditLog entity to CSV line correctly with oldValue = null`() {
        //Given / When
        val result = auditCsvMapper.mapTo(MockAuditLog.AUDIT_LOG.copy(oldValue = null))

        //Then
        assertThat(result).isEqualTo(MockAuditLog.CSV_STRING_LINE_WITH_OLD_VALUE_NULL)
    }

    @Test
    fun `should map full CSV line to full AuditLog entity correctly with changedField = null`() {
        //Given / When
        val result = auditCsvMapper.mapFrom(MockAuditLog.CSV_STRING_LINE_WITH_CHANGED_FIELD_NULL)

        //Then
        assertThat(result).isEqualTo(MockAuditLog.AUDIT_LOG.copy(changedField = null))
    }

    @Test
    fun `should map full AuditLog entity to CSV line correctly with changedField = null`() {
        //Given / When
        val result = auditCsvMapper.mapTo(MockAuditLog.AUDIT_LOG.copy(changedField = null))

        //Then
        assertThat(result).isEqualTo(MockAuditLog.CSV_STRING_LINE_WITH_CHANGED_FIELD_NULL)
    }

    @Test
    fun `should map full CSV line to full AuditLog entity correctly with changedField & oldValue & newValue = null`() {
        //Given / When
        val result = auditCsvMapper.mapFrom(MockAuditLog.CSV_STRING_LINE_MISSING_CHANGED_FIELD_AND_OLD_VALUE_AND_NEW_VALUE)

        //Then
        assertThat(result).isEqualTo(MockAuditLog.AUDIT_LOG.copy(changedField = null, oldValue = null, newValue = null))
    }

    @Test
    fun `should map full AuditLog entity to CSV line correctly with changedField & oldValue & newValue = null`() {
        //Given / When
        val result = auditCsvMapper.mapTo(MockAuditLog.AUDIT_LOG.copy(changedField = null, oldValue = null, newValue = null))

        //Then
        assertThat(result).isEqualTo(MockAuditLog.CSV_STRING_LINE_MISSING_CHANGED_FIELD_AND_OLD_VALUE_AND_NEW_VALUE)
    }
}
