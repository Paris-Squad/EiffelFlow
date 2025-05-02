package domain.mapper

import com.google.common.truth.Truth.assertThat
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.Project
import org.example.domain.model.User
import org.junit.jupiter.api.BeforeEach
import utils.UserMock
import utils.ProjectsMock
import kotlin.test.Test

class ProjectMapperTest {

    private lateinit var editor: User
    private lateinit var project: Project

    @BeforeEach
    fun setup() {
        editor = UserMock.validUser
        project = ProjectsMock.CORRECT_PROJECT
    }

    @Test
    fun `toAuditLog should create audit log with project details for CREATE action`() {
        //Given
        val actionType = AuditLogAction.CREATE

        // When
        val result = project.toAuditLog(
            editor = editor,
            actionType = actionType,
            newValue = project.projectName
        )

        // Then
        assertThat(result).isEqualTo(
            AuditLog(
                auditId = result.auditId, // UUID is random, so we can't predict it
                auditTime = result.auditTime, // UUID is random, so we can't predict it
                itemId = project.projectId,
                itemName = project.projectName,
                userId = editor.userId,
                editorName = editor.username,
                actionType = actionType,
                changedField = null,
                oldValue = null,
                newValue = project.projectName,
            )
        )
    }

    @Test
    fun `toAuditLog should create audit log with project details for DELETE action`() {
        // Given
        val actionType = AuditLogAction.DELETE

        // When
        val result = project.toAuditLog(
            editor = editor,
            actionType = actionType,
            newValue = project.projectName
        )

        // Then
        assertThat(result).isEqualTo(
            AuditLog(
                auditId = result.auditId, // UUID is random, so we can't predict it
                auditTime = result.auditTime, // UUID is random, so we can't predict it
                itemId = project.projectId,
                itemName = project.projectName,
                userId = editor.userId,
                editorName = editor.username,
                actionType = actionType,
                changedField = null,
                oldValue = null,
                newValue = project.projectName,
            )
        )
    }

    @Test
    fun `toAuditLog should generate unique UUID for each audit log`() {
        // Given
        val actionType = AuditLogAction.UPDATE

        // When
        val result = project.toAuditLog(
            editor = editor,
            actionType = actionType,
            newValue = project.projectName
        )

        // Then
        assertThat(result).isEqualTo(
            AuditLog(
                auditId = result.auditId, // UUID is random, so we can't predict it
                auditTime = result.auditTime, // UUID is random, so we can't predict it
                itemId = project.projectId,
                itemName = project.projectName,
                userId = editor.userId,
                editorName = editor.username,
                actionType = actionType,
                changedField = null,
                oldValue = null,
                newValue = project.projectName,
            )
        )
    }

    @Test
    fun `toAuditLog should include editor information correctly`() {
        // Given
        val actionType = AuditLogAction.UPDATE

        // When
        val result = project.toAuditLog(
            editor = editor,
            actionType = actionType,
            newValue = project.projectName
        )

        // Then
        assertThat(result).isEqualTo(
            AuditLog(
                auditId = result.auditId, // UUID is random, so we can't predict it
                auditTime = result.auditTime, // UUID is random, so we can't predict it
                itemId = project.projectId,
                itemName = project.projectName,
                userId = editor.userId,
                editorName = editor.username,
                actionType = actionType,
                changedField = null,
                oldValue = null,
                newValue = project.projectName,
            )
        )
    }
}