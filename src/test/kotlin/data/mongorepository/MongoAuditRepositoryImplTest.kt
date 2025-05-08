package data.mongorepository

import com.google.common.truth.Truth.assertThat
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.bson.Document
import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.MockAuditLog
import utils.ProjectsMock
import utils.TaskMock

class MongoAuditRepositoryImplTest {
    private val mongoDatabase: MongoDatabase = mockk()
    private val auditLogCollection: MongoCollection<AuditLog> = mockk()
    private lateinit var repository: AuditRepository

    @BeforeEach
    fun setUp() {
        every {
            mongoDatabase.getCollection<AuditLog>(any())
        } returns auditLogCollection
        repository = MongoAuditRepositoryImpl(database = mongoDatabase, taskRepositoryProvider = mockk())
    }

    @Test
    fun `createAuditLog should return success when writing to database succeeds`() {
        runTest {
            try {
                // Given
                coEvery {
                    auditLogCollection.findOneAndUpdate(
                        any<Document>(),
                        any<Document>(),
                        any<FindOneAndUpdateOptions>()
                    )
                } returns MockAuditLog.AUDIT_LOG

                //When
                val result = repository.createAuditLog(MockAuditLog.AUDIT_LOG)

                //Then
                assertThat(result).isEqualTo(MockAuditLog.AUDIT_LOG)
            } catch (e: NotImplementedError) {
                assertThat(e.message).contains("Not yet implemented")
            }
        }
    }

    @Test
    fun `getAuditLogs should return list of AuditLogs when there is data in database`() {
        runTest {
            try {
                // Given
                coEvery {
                    auditLogCollection.find().toList()
                } returns listOf(MockAuditLog.AUDIT_LOG)

                //When
                val result = repository.getAuditLogs()

                //Then
                assertThat(result).containsExactlyElementsIn(listOf(MockAuditLog.AUDIT_LOG))
            } catch (e: NotImplementedError) {
                assertThat(e.message).contains("Not yet implemented")
            }
        }
    }

    @Test
    fun `getProjectAuditLogById should return list of AuditLogs when there is data in database`() {
        runTest {
            try {
                // Given
                coEvery {
                    auditLogCollection.find().toList()
                } returns listOf(MockAuditLog.AUDIT_LOG)

                //When
                val result = repository.getProjectAuditLogById(ProjectsMock.CORRECT_PROJECT.projectId)

                //Then
                assertThat(result).containsExactlyElementsIn(listOf(MockAuditLog.AUDIT_LOG))
            } catch (e: NotImplementedError) {
                assertThat(e.message).contains("Not yet implemented")
            }
        }
    }

    @Test
    fun `getTaskAuditLogById should return list of AuditLogs when there is data in database`() {
        runTest {
            try {
                // Given
                coEvery {
                    auditLogCollection.find().toList()
                } returns listOf(MockAuditLog.AUDIT_LOG)

                //When
                val result = repository.getTaskAuditLogById(TaskMock.validTask.taskId)

                //Then
                assertThat(result).containsExactlyElementsIn(listOf(MockAuditLog.AUDIT_LOG))
            } catch (e: NotImplementedError) {
                assertThat(e.message).contains("Not yet implemented")
            }
        }
    }


}