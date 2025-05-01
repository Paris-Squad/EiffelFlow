package utils
import kotlinx.datetime.LocalDateTime
import org.example.domain.model.entities.RoleType
import org.example.domain.model.entities.State
import org.example.domain.model.entities.Task
import java.util.*

object TaskMock {
    private val mockTaskId: UUID = UUID.randomUUID()
    private val mockCreatorId = UUID.randomUUID()
    private val mockProjectId = UUID.randomUUID()
    private val mockAssignedId = UUID.randomUUID()
    private val mockCreatedAt = LocalDateTime(2023, 1, 1, 12, 0)

    val validTask = Task(
        taskId = mockTaskId,
        title = "Prepare Monthly Report",
        description = "Collect project updates and compile them into a monthly report",
        createdAt = mockCreatedAt,
        creatorId = mockCreatorId,
        projectId = mockProjectId,
        assignedId = mockAssignedId,
        role = RoleType.MATE,
        state = State(name = "todo")
    )

}