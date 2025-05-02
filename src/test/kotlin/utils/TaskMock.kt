package utils
import kotlinx.datetime.LocalDateTime
import org.example.domain.model.AuditLogAction
import org.example.domain.model.AuditLog
import org.example.domain.model.RoleType
import org.example.domain.model.TaskState
import org.example.domain.model.Task
import java.util.UUID

object TaskMock {
    val mockTaskId: UUID = UUID.randomUUID()
    private val mockCreatorId = UUID.randomUUID()
    private val mockProjectId = UUID.randomUUID()
    private val mockAssignedId = UUID.randomUUID()
    private val mockCreatedAt = LocalDateTime(2023, 1, 1, 12, 0)

    val validTask = Task(
        taskId = mockTaskId,
        title = "Find Devil Devil Bareq",
        description = "try to find Devil Bareq and put him in the jail",
        createdAt = mockCreatedAt,
        creatorId = mockCreatorId,
        projectId = mockProjectId,
        assignedId = mockAssignedId,
        role = RoleType.MATE,
        state = TaskState(name = "todo")
    )

    val inProgressTask = Task(
        taskId = UUID.randomUUID(),
        title = "Investigate Criminal Case",
        description = "Investigate the criminal activities in the area",
        createdAt = mockCreatedAt,
        creatorId = mockCreatorId,
        projectId = mockProjectId,
        assignedId = mockAssignedId,
        role = RoleType.MATE,
        state = TaskState(name = "in progress")
    )

    val ValidTaskCSV =
        "Find Devil Devil Bareq,try to find Devil Bareq and put him in the jail,2023-01-01T12:00:00,${validTask.creatorId},${validTask.projectId},${validTask.assignedId},MATE,TODO"

    val validAuditLog = AuditLog(
        itemId = validTask.taskId,
        itemName = validTask.title,
        userId = validTask.creatorId,
        editorName = "Admin",
        actionType = AuditLogAction.CREATE,
        auditTime = validTask.createdAt,
        changedField = null,
        oldValue = null,
        newValue = validTask.title
    )
}