package domain.mapper

import com.google.common.truth.Truth.assertThat
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLog
import org.example.domain.model.AuditLogAction
import utils.TaskMock
import utils.UserMock
import kotlin.test.Test

class TaskMapperTest {

    @Test
    fun `toAuditLog should create audit log with all fields when all parameters are provided`() {
        // Given
        val actionType = AuditLogAction.UPDATE
        val changedField = "description"
        val oldValue = "old description"
        val newValue = "new description"

        // When
        val result = TaskMock.validTask.toAuditLog(
            editor = UserMock.validUser,
            actionType = actionType,
            changedField = changedField,
            oldValue = oldValue,
            newValue = newValue
        )

        // Then
        assertThat(result).isEqualTo(
            AuditLog(
                auditId = result.auditId,
                auditTime = result.auditTime,
                itemId = TaskMock.validTask.taskId,
                itemName = TaskMock.validTask.title,
                userId = UserMock.validUser.userId,
                editorName = UserMock.validUser.username,
                actionType = AuditLogAction.UPDATE,
                changedField = changedField,
                oldValue = oldValue,
                newValue = newValue
            )
        )
    }

    @Test
    fun `toAuditLog should create audit log with null changedField and oldValue when not provided`() {
        // When
        val result = TaskMock.validTask.toAuditLog(
            editor = UserMock.validUser,
            actionType = AuditLogAction.CREATE,
            newValue = TaskMock.validTask.title
        )

        // Then
        assertThat(result).isEqualTo(
            AuditLog(
                auditId = result.auditId,
                auditTime = result.auditTime,
                itemId = TaskMock.validTask.taskId,
                itemName = TaskMock.validTask.title,
                userId = UserMock.validUser.userId,
                editorName = UserMock.validUser.username,
                actionType = AuditLogAction.CREATE,
                changedField = null,
                oldValue = null,
                newValue = TaskMock.validTask.title
            )
        )
    }

    @Test
    fun `toAuditLog should create audit log with different action types`() {
        // Given
        val newValue = "DELETED"
        val actionType = AuditLogAction.DELETE

        // When
        val result = TaskMock.validTask.toAuditLog(
            editor = UserMock.validUser,
            actionType = actionType,
            newValue = newValue
        )

        // Then
        assertThat(result).isEqualTo(
            AuditLog(
                auditId = result.auditId,
                auditTime = result.auditTime,
                itemId = TaskMock.validTask.taskId,
                itemName = TaskMock.validTask.title,
                userId = UserMock.validUser.userId,
                editorName = UserMock.validUser.username,
                actionType = actionType,
                changedField = null,
                oldValue = null,
                newValue = newValue
            )
        )
    }
}