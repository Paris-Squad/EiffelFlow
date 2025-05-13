package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.data.utils.SessionManger
import org.example.domain.model.RoleType
import org.example.domain.model.TaskState
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import org.example.domain.usecase.task.EditTaskUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.TaskMock.inProgressTask
import utils.TaskMock.validTask
import utils.UserMock
import java.util.*

class EditTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var editTaskUseCase: EditTaskUseCase
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private val sessionManger: SessionManger = mockk(relaxed = true)



    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        editTaskUseCase = EditTaskUseCase(taskRepository = taskRepository , auditRepository = auditRepository)
    }

    @Test
    fun `editTask should successfully update task when changes are detected`() {
        runTest {
            every { sessionManger.getUser() } returns UserMock.validUser
            coEvery { taskRepository.getTaskById(inProgressTask.taskId) } returns validTask
            coEvery { taskRepository.updateTask(inProgressTask, validTask, any()) } returns inProgressTask


            val result = editTaskUseCase.editTask(inProgressTask)

            assertThat(result).isEqualTo(inProgressTask)
            coVerify {
                taskRepository.updateTask(
                    inProgressTask, validTask, match { it.contains("state") })
            }
        }
    }

    @Test
    fun `editTask should fail with IOException when no changes detected`() {
        runTest {
            every { sessionManger.getUser() } returns UserMock.validUser
            coEvery { taskRepository.getTaskById(validTask.taskId) } returns validTask

            val exception = assertThrows<EiffelFlowException.IOException> {
                editTaskUseCase.editTask(validTask)
            }

            assertThat(exception.message).isEqualTo("No changes detected")
        }
    }
    @Test
    fun `editTask should fail when task is not found`() {
        runTest {
            coEvery { taskRepository.getTaskById(validTask.taskId) } throws EiffelFlowException.NotFoundException("Task not found")

            val exception = assertThrows<EiffelFlowException.NotFoundException> {
                editTaskUseCase.editTask(validTask)
            }

            assertThat(exception.message).isEqualTo("Task not found")
        }
    }

    @Test
    fun `editTask should identify title changes`() {
        runTest {
            every { sessionManger.getUser() } returns UserMock.validUser
            val originalTask = validTask
            val updatedTask = originalTask.copy(title = "Updated Title")

            coEvery { taskRepository.getTaskById(updatedTask.taskId) } returns originalTask
            coEvery { taskRepository.updateTask(updatedTask, originalTask, any()) } returns updatedTask

            val result = editTaskUseCase.editTask(updatedTask)

            assertThat(result).isEqualTo(updatedTask)

            coVerify {
                taskRepository.updateTask(
                    updatedTask,
                    originalTask,

                    match { it.contains("title") }
                )
            }
        }
    }
    @Test
    fun `editTask should identify description changes`() {
        runTest {
            every { sessionManger.getUser() } returns UserMock.validUser
            val originalTask = validTask
            val updatedTask = originalTask.copy(description = "Updated description")

            coEvery { taskRepository.getTaskById(updatedTask.taskId) } returns originalTask
            coEvery { taskRepository.updateTask(updatedTask, originalTask, any()) } returns updatedTask


            val result = editTaskUseCase.editTask(updatedTask)

            assertThat(result).isEqualTo(updatedTask)

            coVerify {
                taskRepository.updateTask(
                    updatedTask,
                    originalTask,

                    match { it.contains("description") })
            }
        }
    }
    @Test
    fun `editTask should identify assignee changes`() {
        runTest {
            every { sessionManger.getUser() } returns UserMock.validUser
            val originalTask = validTask
            val updatedTask = originalTask.copy(assignedId = UUID.randomUUID())

            coEvery { taskRepository.getTaskById(updatedTask.taskId) } returns originalTask
            coEvery { taskRepository.updateTask(updatedTask, originalTask, any()) } returns updatedTask

            val result = editTaskUseCase.editTask(updatedTask)

            assertThat(result).isEqualTo(updatedTask)

            coVerify {
                taskRepository.updateTask(
                    updatedTask,
                    originalTask,

                    match { it.contains("assignedId") }
                )
            }
        }
    }
    @Test
    fun `editTask should identify role changes`() {
        runTest {
            every { sessionManger.getUser() } returns UserMock.validUser
            val originalTask = validTask
            val updatedTask = originalTask.copy(role = RoleType.ADMIN)

            coEvery { taskRepository.getTaskById(updatedTask.taskId) } returns originalTask
            coEvery { taskRepository.updateTask(updatedTask, originalTask, any()) } returns updatedTask


            val result = editTaskUseCase.editTask(updatedTask)

            assertThat(result).isEqualTo(updatedTask)

            coVerify {
                taskRepository.updateTask(
                    updatedTask,
                    originalTask,

                    match { it.contains("role") }
                )
            }
        }
    }

    @Test
    fun `editTask should identify project changes`() {
        runTest {
            every { sessionManger.getUser() } returns UserMock.validUser
            val originalTask = validTask
            val updatedTask = originalTask.copy(projectId = UUID.randomUUID())

            coEvery { taskRepository.getTaskById(updatedTask.taskId) } returns originalTask
            coEvery { taskRepository.updateTask(updatedTask, originalTask, any()) } returns updatedTask


            val result = editTaskUseCase.editTask(updatedTask)

            assertThat(result).isEqualTo(updatedTask)

            coVerify {
                taskRepository.updateTask(
                    updatedTask,
                    originalTask,

                    match { it.contains("projectId") }
                )
            }
        }
    }
    @Test
    fun `editTask should identify state changes`() {
        runTest {
            every { sessionManger.getUser() } returns UserMock.validUser
            val originalTask = validTask
            val updatedTask = originalTask.copy(state = TaskState(name = "in progress"))

            coEvery { taskRepository.getTaskById(updatedTask.taskId) } returns originalTask
            coEvery { taskRepository.updateTask(updatedTask, originalTask, any()) } returns updatedTask


            val result = editTaskUseCase.editTask(updatedTask)

            assertThat(result).isEqualTo(updatedTask)

            coVerify {
                taskRepository.updateTask(
                    updatedTask,
                    originalTask,

                    match { it.contains("state") }
                )
            }
        }
    }
    @Throws
    @Test
    fun `editTask  should threw IOException when no fields changed`() {
        runTest {
            val originalTask = validTask
            val updatedTask = validTask.copy() // No changes

            coEvery { taskRepository.getTaskById(updatedTask.taskId) } returns originalTask

            val exception = assertThrows<EiffelFlowException.IOException> {
                editTaskUseCase.editTask(updatedTask)
            }

            assertThat(exception.message).isEqualTo("No changes detected")
        }
    }
    @Test
    fun `editTask should identify when multiple fields are updated`() {
        runTest {
            every { sessionManger.getUser() } returns UserMock.validUser
            val originalTask = validTask
            val updatedTask = originalTask.copy(
                title = "Updated Title", description = "Updated Description", assignedId = UUID.randomUUID()
            )

            coEvery { taskRepository.getTaskById(updatedTask.taskId) } returns originalTask
            coEvery { taskRepository.updateTask(updatedTask, originalTask, any()) } returns updatedTask


            val result = editTaskUseCase.editTask(updatedTask)

            assertThat(result).isEqualTo(updatedTask)

            coVerify {
                taskRepository.updateTask(
                    updatedTask, originalTask, match {
                        it.contains("title") &&
                                it.contains("description") &&
                                it.contains("assignedId")
                    })
            }
        }
    }
}