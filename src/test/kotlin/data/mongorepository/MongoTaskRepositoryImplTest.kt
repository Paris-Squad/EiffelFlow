package data.mongorepository

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
import org.example.data.MongoCollections
import org.example.data.storage.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.AuditLog
import org.example.domain.model.Task
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.TaskMock
import utils.UserMock
import java.util.UUID

class MongoTaskRepositoryImplTest {

    private val sessionManger: SessionManger = mockk(relaxed = true)
    private lateinit var tasksCollection: MongoCollection<Task>
    private lateinit var auditRepository: AuditRepository
    private lateinit var userRepository: TaskRepository

    @BeforeEach
    fun setup() {
        tasksCollection = mockk(relaxed = true)
        auditRepository = mockk(relaxed = true)

        val mockDatabase = mockk<MongoDatabase>()
        every {
            mockDatabase.getCollection<Task>(MongoCollections.TASKS)
        } returns tasksCollection

        every { sessionManger.getUser() } returns UserMock.adminUser

        userRepository = MongoTaskRepositoryImpl(mockDatabase, auditRepository)
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
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()
        coEvery {
            tasksCollection.insertOne(eq(TaskMock.validTask), any())
        } returns mockk()

        //When
        val result = userRepository.createTask(TaskMock.validTask)

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
        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()

        //When /Then
        assertThrows(EiffelFlowException.IOException::class.java) {
            runBlocking { userRepository.createTask(TaskMock.validTask) }
        }
    }

    @Test
    fun `createTask should throw Exception when audit log creation fails`() = runTest {
        //Given
        val mockFindFlow = mockk<FindFlow<Task>>()

        coEvery { tasksCollection.find(any<Bson>()) } returns mockFindFlow

        coEvery { mockFindFlow.collect(any()) } coAnswers {
        }
        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()
        coEvery {
            tasksCollection.insertOne(eq(TaskMock.validTask), any())
        } throws EiffelFlowException.IOException("Custom exception")

        //When then
        assertThrows<EiffelFlowException.IOException> {
            userRepository.createTask(TaskMock.validTask)
        }

    }

    @Test
    fun `createTask should throw Exception when write to mongodb fails`() = runTest {
        //Given
        val mockFindFlow = mockk<FindFlow<Task>>()

        coEvery { tasksCollection.find(any<Bson>()) } returns mockFindFlow

        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()

        coEvery {
            tasksCollection.insertOne(eq(TaskMock.validTask), any())
        } returns mockk()

        assertThrows<EiffelFlowException.IOException> {
            userRepository.createTask(TaskMock.validTask)
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
        val result = userRepository.updateTask(
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
                userRepository.updateTask(
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
                userRepository.updateTask(
                    task = TaskMock.inProgressTask,
                    oldTask = TaskMock.validTask,
                    changedField = "state"
                )
            }
        }
    }

    @Test
    fun `updateTask should throw Exception when audit log creation fails`() {
        runTest {
            // Given
            coEvery {
                auditRepository.createAuditLog(any())
            } throws Throwable("Can't Update Task")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                userRepository.updateTask(
                    task = TaskMock.inProgressTask,
                    oldTask = TaskMock.validTask,
                    changedField = "role"
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
        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()

        // When
        val result = userRepository.deleteTask(TaskMock.inProgressTask.taskId)

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
                userRepository.deleteTask(UUID.randomUUID())
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
                userRepository.deleteTask(TaskMock.validTask.taskId)
            }
        }
    }

    @Test
    fun `deleteTask should throw Exception when audit log creation fails`() {
        runTest {
            // Given
            coEvery {
                auditRepository.createAuditLog(any())
            } throws Throwable("Can't Delete Task")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                userRepository.deleteTask(TaskMock.validTask.taskId)
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
            val result = userRepository.getTaskById(TaskMock.validTask.taskId)

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
                userRepository.getTaskById(UUID.randomUUID())
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
                userRepository.getTaskById(TaskMock.validTask.taskId)
            }
        }
    }
    //endregion

    // region getTasks
    @Test
    fun `getTasks should return list of users`() {
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
            val result = userRepository.getTasks()

            //Then
            assertThat(result).containsExactlyElementsIn(listOf(TaskMock.validTask))
        }
    }

    @Test
    fun `getTasks should return empty list of users when DB is empty`() {
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
            val result = userRepository.getTasks()

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
                userRepository.getTasks()
            }
        }
    }
    //endregion

}