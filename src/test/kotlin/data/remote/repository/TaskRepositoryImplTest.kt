package data.remote.repository

import com.google.common.truth.Truth.assertThat
import com.mongodb.MongoException
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bson.conversions.Bson
import org.example.data.remote.MongoCollections
import org.example.data.remote.mapper.TaskMapper
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Task
import org.example.domain.repository.TaskRepository
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.TaskMock
import utils.UserMock
import java.util.UUID

class TaskRepositoryImplTest {

    private val taskMapper: TaskMapper = mockk(relaxed = true)
    private val sessionManger: SessionManger = mockk(relaxed = true)
    private lateinit var tasksCollection: MongoCollection<Task>
    private lateinit var taskRepository: TaskRepository

    @BeforeEach
    fun setup() {
        tasksCollection = mockk(relaxed = true)

        val mockDatabase = mockk<MongoDatabase>()
        every {
            mockDatabase.getCollection<Task>(MongoCollections.TASKS)
        } returns tasksCollection

        every { sessionManger.getUser() } returns UserMock.adminUser

        taskRepository = TaskRepositoryImpl(
           database =  mockDatabase,
            taskMapper = taskMapper
        )
    }

    //region createTask
    @Test
    fun `createTask should insert task if not exists`() = runTest {
        //Given
        val mockFindFlow = mockk<FindFlow<Task>>()

        coEvery { tasksCollection.find(any<Bson>()) } returns mockFindFlow

        coEvery { mockFindFlow.collect(any()) } coAnswers {
        }
        coEvery {
            tasksCollection.insertOne(eq(TaskMock.validTask), any())
        } returns mockk()

        //When
        val result = taskRepository.createTask(TaskMock.validTask)

        //Then
        assertThat(result).isEqualTo(TaskMock.validTask)
    }

    @Test
    fun `createTask should throw if task already exists`() = runTest {
        //Given
        val mockFindFlow = mockk<FindFlow<Task>>()
        coEvery { tasksCollection.find(any<Bson>()) } returns mockFindFlow
        coEvery { mockFindFlow.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<Task>>(0)
            collector.emit(TaskMock.validTask)
        }

        //When /Then
        assertThrows(EiffelFlowException.IOException::class.java) {
            runBlocking { taskRepository.createTask(TaskMock.validTask) }
        }
    }

    @Test
    fun `createTask should throw Exception when write to mongodb fails`() = runTest {
        //Given
        val mockFindFlow = mockk<FindFlow<Task>>()

        coEvery { tasksCollection.find(any<Bson>()) } returns mockFindFlow

        coEvery {
            tasksCollection.insertOne(eq(TaskMock.validTask), any())
        } returns mockk()

        assertThrows<EiffelFlowException.IOException> {
            taskRepository.createTask(TaskMock.validTask)
        }
    }
    //endregion

    //region updateTask
    @Test
    fun `updateTask should update correct if the item exists`() = runTest {
        //Given
        coEvery {
            tasksCollection.findOneAndUpdate(
                any<Bson>(),
                any<Bson>(),
                any()
            )
        } returns TaskMock.validTask

        // When
        val result = taskRepository.updateTask(
            task = TaskMock.inProgressTask,
            oldTask = TaskMock.validTask,
            changedField = "name"
        )

        assertThat(result).isEqualTo(TaskMock.inProgressTask)
    }

    @Test
    fun `updateTask should throw Exception when task is not found`() {
        runTest {
            // Given
            coEvery {
                tasksCollection.findOneAndUpdate(
                    any<Bson>(),
                    any<Bson>(),
                    any()
                )
            } returns null

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                taskRepository.updateTask(
                    task = TaskMock.inProgressTask,
                    oldTask = TaskMock.validTask,
                    changedField = "description"
                )
            }
        }
    }

    @Test
    fun `updateTask should return Exception when writing to DB fails`() {
        runTest {
            // Given
            coEvery {
                tasksCollection.findOneAndUpdate(
                    any<Bson>(),
                    any<List<Bson>>(),
                    any()
                )
            } throws MongoException("Can't update this task")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                taskRepository.updateTask(
                    task = TaskMock.inProgressTask,
                    oldTask = TaskMock.validTask,
                    changedField = "state"
                )
            }
        }
    }
    //endregion

    //region deleteTask
    @Test
    fun `deleteTask should return the deleted task on success`() = runTest {
        coEvery {
            tasksCollection.findOneAndDelete(
                any<Bson>(),
                any()
            )
        } returns TaskMock.inProgressTask

        // When
        val result = taskRepository.deleteTask(TaskMock.inProgressTask.taskId)

        // Then
        assertThat(result).isEqualTo(TaskMock.inProgressTask)
    }

    @Test
    fun `deleteTask should throw Exception when task is not found`() {
        runTest {
            //Given
            coEvery {
                tasksCollection.findOneAndDelete(
                    any<Bson>(),
                    any()
                )
            } returns null

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                taskRepository.deleteTask(UUID.randomUUID())
            }
        }
    }

    @Test
    fun `deleteTask should throw Exception when writing to DB fails`() {
        runTest {
            // Given
            coEvery {
                tasksCollection.findOneAndDelete(
                    any<Bson>(),
                    any()
                )
            } throws MongoException("Can't delete this task")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                taskRepository.deleteTask(TaskMock.validTask.taskId)
            }
        }
    }

    //endregion


    //region getTaskById
    @Test
    fun `getTaskById should return the task on success`() {
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<Task>>()

            coEvery { tasksCollection.find(any<Bson>()) } returns mockFindFlow

            coEvery { mockFindFlow.collect(any()) } coAnswers {
                val collector = arg<FlowCollector<Task>>(0)
                collector.emit(TaskMock.validTask)
            }

            coEvery {
                tasksCollection.find()
            } returns mockFindFlow

            //When
            val result = taskRepository.getTaskById(TaskMock.validTask.taskId)

            //Then
            assertThat(result).isEqualTo(TaskMock.validTask)
        }
    }

    @Test
    fun `getTaskById should throw Exception when task is not found`() {
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<Task>>()

            coEvery { tasksCollection.find(any<Bson>()) } returns mockFindFlow

            coEvery { mockFindFlow.collect(any()) } coAnswers {

            }

            coEvery {
                tasksCollection.find()
            } returns mockFindFlow

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                taskRepository.getTaskById(UUID.randomUUID())
            }
        }
    }

    @Test
    fun `getTaskById should throw Exception when DB operation fails`() {
        runTest {
            // Given
            coEvery {
                tasksCollection.find()
            } throws MongoException("Can't get Task")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                taskRepository.getTaskById(TaskMock.validTask.taskId)
            }
        }
    }
    //endregion

    // region getTasks
    @Test
    fun `getTasks should return list of tasks`() {
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<Task>>()

            coEvery { tasksCollection.find(any<Bson>()) } returns mockFindFlow
            coEvery { mockFindFlow.collect(any()) } coAnswers {
                val collector = arg<FlowCollector<Task>>(0)
                collector.emit(TaskMock.validTask)
            }

            coEvery {
                tasksCollection.find()
            } returns mockFindFlow

            //When
            val result = taskRepository.getTasks()

            //Then
            assertThat(result).containsExactlyElementsIn(listOf(TaskMock.validTask))
        }
    }

    @Test
    fun `getTasks should return empty list of tasks when DB is empty`() {
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<Task>>()

            coEvery { tasksCollection.find(any<Bson>()) } returns mockFindFlow
            coEvery { mockFindFlow.collect(any()) } coAnswers {
            }

            coEvery {
                tasksCollection.find()
            } returns mockFindFlow

            //When
            val result = taskRepository.getTasks()

            //Then
            assertThat(result).containsExactlyElementsIn(emptyList<Task>())
        }
    }

    @Test
    fun `getTasks should throw Exception when DB operation fails`() {
        runTest {
            // Given
            coEvery {
                tasksCollection.find()
            } throws MongoException("Can't get Task")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                taskRepository.getTasks()
            }
        }
    }
    //endregion

}