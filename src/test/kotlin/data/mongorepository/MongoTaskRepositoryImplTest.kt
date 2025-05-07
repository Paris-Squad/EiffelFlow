package data.mongorepository

import com.google.common.truth.Truth.assertThat
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import domain.usecase.task.TaskMock
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.bson.Document
import org.example.data.storage.SessionManger
import org.example.domain.model.Task
import org.example.domain.repository.AuditRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.UserMock
import java.util.*

class MongoTaskRepositoryImplTest {

    private val sessionManger: SessionManger = mockk(relaxed = true)
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private val mongoDatabase: MongoDatabase = mockk()
    private val taskCollection: MongoCollection<Task> = mockk()
    private lateinit var repository: MongoTaskRepositoryImpl

    @BeforeEach
    fun setUp() {
        every {
            mongoDatabase.getCollection<Task>(any())
        } returns taskCollection
        repository = MongoTaskRepositoryImpl(
            database = mongoDatabase,
            auditRepository = auditRepository
        )
    }

    @Test
    fun `createTask should return the created task`() {
        runTest {
            try {
                // Given
                every { sessionManger.getUser() } returns UserMock.adminUser
                coEvery {
                    taskCollection.findOneAndUpdate(
                        any<Document>(),
                        any<Document>(),
                        any<FindOneAndUpdateOptions>()
                    )
                } returns TaskMock.validTask

                //When
                val result = repository.createTask(TaskMock.validTask)

                //Then
                assertThat(result).isEqualTo(TaskMock.validTask)
            } catch (e: NotImplementedError) {
                assertThat(e.message).contains("Not yet implemented")
            }
        }
    }

    @Test
    fun `updateTask should return success if the task is updated`() {
        runTest {
            try {
                // Given
                every { sessionManger.getUser() } returns UserMock.adminUser
                val oldTask = TaskMock.validTask.copy(description = "Old Description")
                val updatedTask = TaskMock.validTask.copy(description = "New Description")
                coEvery {
                    taskCollection.findOneAndReplace(any(), any())
                } returns updatedTask

                //When
                val result = repository.updateTask(updatedTask, oldTask, "description")

                //Then
                assertThat(result).isEqualTo(updatedTask)
            } catch (e: NotImplementedError) {
                assertThat(e.message).contains("Not yet implemented")
            }
        }
    }

    @Test
    fun `deleteTask should return the deleted task`() {
        runTest {
            try {
                // Given
                every { sessionManger.getUser() } returns UserMock.adminUser
                coEvery {
                    taskCollection.findOneAndDelete(any())
                } returns TaskMock.validTask

                //When
                val result = repository.deleteTask(TaskMock.validTask.taskId)

                //Then
                assertThat(result).isEqualTo(TaskMock.validTask)
            } catch (e: NotImplementedError) {
                assertThat(e.message).contains("Not yet implemented")
            }
        }
    }

    @Test
    fun `getTasks should return list of tasks`() {
        runTest {
            try {
                // Given
                every { sessionManger.getUser() } returns UserMock.adminUser
                val tasks = listOf(
                    TaskMock.validTask,
                    TaskMock.validTask.copy(taskId = UUID.randomUUID(), title = "Task 2")
                )
                coEvery {
                    taskCollection.find().toList()
                } returns tasks

                //When
                val result = repository.getTasks()

                //Then
                assertThat(result).containsExactlyElementsIn(tasks)
            } catch (e: NotImplementedError) {
                assertThat(e.message).contains("Not yet implemented")
            }
        }
    }

    @Test
    fun `getTaskById should return task when found`() {
        runTest {
            try {
                // Given
                val query = eq("taskId", TaskMock.validTask.taskId)
                every { sessionManger.getUser() } returns UserMock.adminUser
                coEvery {
                    taskCollection.find(query).firstOrNull()
                } returns TaskMock.validTask

                //When
                val result = repository.getTaskById(TaskMock.validTask.taskId)

                //Then
                assertThat(result).isEqualTo(TaskMock.validTask)
            } catch (e: NotImplementedError) {
                assertThat(e.message).contains("Not yet implemented")
            }
        }
    }

}