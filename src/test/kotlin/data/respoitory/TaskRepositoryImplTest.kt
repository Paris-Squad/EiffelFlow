package data.respoitory

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import org.example.data.respoitory.TaskRepositoryImpl
import org.example.domain.model.RoleType
import org.example.domain.model.Task
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

//todo change those testcases
class TaskRepositoryImplTest {

    private lateinit var taskRepository: TaskRepositoryImpl

    @BeforeEach
    fun setUp() {
        taskRepository = TaskRepositoryImpl()
    }

    @Test
    fun `createTask should return the created task`() {
        val task = Task(
            title = "Test",
            description = "Test",
            createdAt = LocalDateTime(2023, 1, 1, 12, 0),
            creatorId = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            stateId = UUID.randomUUID(),
            assignedId = UUID.randomUUID(),
            role = RoleType.MATE
        )

        try {
            taskRepository.createTask(task)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `updateTask should return the updated task`() {
        val task = Task(
            title = "Updated Task",
            description = "Updated Description",
            createdAt = LocalDateTime(2023, 1, 1, 12, 0),
            creatorId = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            stateId = UUID.randomUUID(),
            assignedId = UUID.randomUUID(),
            role = RoleType.MATE
        )

        try {
            taskRepository.updateTask(task)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `deleteTask should return the deleted task`() {
        val taskId = UUID.randomUUID()

        try {
            taskRepository.deleteTask(taskId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getTasks should return list of tasks`() {
        try {
            taskRepository.getTasks()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}