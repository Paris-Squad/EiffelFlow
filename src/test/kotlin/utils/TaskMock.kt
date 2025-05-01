package utils
import kotlinx.datetime.LocalDateTime
import org.example.domain.model.entities.RoleType
import org.example.domain.model.entities.State
import org.example.domain.model.entities.Task
import java.util.UUID
import java.util.*

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
        state = State(name = "todo")
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
        state = State(name = "in progress")
    )

    val ValidTaskCSV =
        "Find Devil Devil Bareq,try to find Devil Bareq and put him in the jail,2023-01-01T12:00:00,${validTask.creatorId},${validTask.projectId},${validTask.assignedId},MATE,TODO"
}