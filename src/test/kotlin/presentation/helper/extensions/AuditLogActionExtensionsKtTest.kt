package presentation.helper.extensions

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.example.domain.model.AuditLogAction
import org.example.presentation.helper.extensions.toDisplayName

class AuditLogActionExtensionsKtTest {

    @Test
    fun `should return Created when the AuditLog is CREATE`() {
        // Given
        val actionType = AuditLogAction.CREATE

        // When
        val result = actionType.toDisplayName()

        // Then
        assertThat(result).isEqualTo("Created")
    }

    @Test
    fun `should return Updated when the AuditLog is UPDATE`() {
        // Given
        val actionType = AuditLogAction.UPDATE

        // When
        val result = actionType.toDisplayName()

        // Then
        assertThat(result).isEqualTo("Updated")
    }

    @Test
    fun `should return Deleted when the AuditLog is DELETE`() {
        // Given
        val actionType = AuditLogAction.DELETE

        // When
        val result = actionType.toDisplayName()

        // Then
        assertThat(result).isEqualTo("Deleted")
    }
}
