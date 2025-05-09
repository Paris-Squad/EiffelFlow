package data.remote.mongorepository

import com.google.common.truth.Truth.assertThat
import com.mongodb.MongoException
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import data.mongorepository.MongoAuditRepositoryImpl
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bson.conversions.Bson
import org.example.data.remote.MongoCollections
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.TaskRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.MockAuditLog
import utils.TaskMock

class MongoAuditRepositoryImplTest {

    private lateinit var auditCollection: MongoCollection<AuditLog>
    private var taskRepository: Lazy<TaskRepository> = mockk(relaxed = true)
    private lateinit var auditRepository: AuditRepository

    @BeforeEach
    fun setup() {
        auditCollection = mockk(relaxed = true)
        auditRepository = mockk(relaxed = true)

        val mockDatabase = mockk<MongoDatabase>()
        every {
            mockDatabase.getCollection<AuditLog>(MongoCollections.AUDIT_LOGS)
        } returns auditCollection

        auditRepository = MongoAuditRepositoryImpl(
            database = mockDatabase,
            taskRepositoryProvider = taskRepository
        )
    }

    //region createAuditLog
    @Test
    fun `createAuditLog should create AuditLog if not exists`() = runTest {
        //Given
        val mockFindFlow = mockk<FindFlow<AuditLog>>()

        coEvery { auditCollection.find(any<Bson>()) } returns mockFindFlow

        coEvery { mockFindFlow.collect(any()) } coAnswers {
        }
        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()
        coEvery {
            auditCollection.insertOne(eq(MockAuditLog.AUDIT_LOG), any())
        } returns mockk()

        //When
        val result = auditRepository.createAuditLog(MockAuditLog.AUDIT_LOG)

        //Then
        assertThat(result).isEqualTo(MockAuditLog.AUDIT_LOG)
    }

    @Test
    fun `createAuditLog should throw if AuditLog already exists`() = runTest {
        //Given
        val mockFindFlow = mockk<FindFlow<AuditLog>>()
        coEvery { auditCollection.find(any<Bson>()) } returns mockFindFlow
        coEvery { mockFindFlow.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<AuditLog>>(0)
            collector.emit(MockAuditLog.AUDIT_LOG)
        }
        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()

        //When /Then
        Assertions.assertThrows(EiffelFlowException.IOException::class.java) {
            runBlocking { auditRepository.createAuditLog(MockAuditLog.AUDIT_LOG) }
        }
    }

    @Test
    fun `createAuditLog should throw Exception when audit log creation fails`() = runTest {
        //Given
        val mockFindFlow = mockk<FindFlow<AuditLog>>()

        coEvery { auditCollection.find(any<Bson>()) } returns mockFindFlow

        coEvery { mockFindFlow.collect(any()) } coAnswers {
        }
        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()
        coEvery {
            auditCollection.insertOne(eq(MockAuditLog.AUDIT_LOG), any())
        } throws EiffelFlowException.IOException("Custom exception")

        //When then
        assertThrows<EiffelFlowException.IOException> {
            auditRepository.createAuditLog(MockAuditLog.AUDIT_LOG)
        }

    }

    @Test
    fun `createAuditLog should throw Exception when write to mongodb fails`() = runTest {
        //Given
        val mockFindFlow = mockk<FindFlow<AuditLog>>()

        coEvery { auditCollection.find(any<Bson>()) } returns mockFindFlow

        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()

        coEvery {
            auditCollection.insertOne(eq(MockAuditLog.AUDIT_LOG), any())
        } returns mockk()

        assertThrows<EiffelFlowException.IOException> {
            auditRepository.createAuditLog(MockAuditLog.AUDIT_LOG)
        }
    }
    //endregion

    //region getProjectAuditLogById
    @Test
    fun `getProjectAuditLogById should return the AuditLogs on success`() {
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<AuditLog>>()

            coEvery { auditCollection.find(any<Bson>()) } returns mockFindFlow

            coEvery { mockFindFlow.collect(any()) } coAnswers {
                val collector = arg<FlowCollector<AuditLog>>(0)
                MockAuditLog.AUDIT_LOGS.forEach { collector.emit(it) }
            }
            coEvery {
                taskRepository.value.getTasks()
            } returns listOf(
                TaskMock.validTask.copy(projectId = MockAuditLog.AUDIT_LOG.itemId),
                TaskMock.inProgressTask
            )

            coEvery {
                auditCollection.find()
            } returns mockFindFlow

            //When
            val result = auditRepository.getProjectAuditLogById(MockAuditLog.AUDIT_LOG.itemId)

            //Then
            assertThat(result).containsExactlyElementsIn(MockAuditLog.AUDIT_LOGS)
        }
    }

    @Test
    fun `getProjectAuditLogById should throw Exception when DB operation fails`() {
        runTest {
            // Given
            coEvery {
                auditCollection.find(any<Bson>())
            } throws MongoException("Can't get AuditLogs")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                auditRepository.getProjectAuditLogById(MockAuditLog.AUDIT_LOG.itemId)
            }
        }
    }
    //endregion

    //region getTaskAuditLogById
    @Test
    fun `getTaskAuditLogById should return the AuditLogs on success`() {
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<AuditLog>>()

            coEvery { auditCollection.find(any<Bson>()) } returns mockFindFlow

            coEvery { mockFindFlow.collect(any()) } coAnswers {
                val collector = arg<FlowCollector<AuditLog>>(0)
                MockAuditLog.AUDIT_LOGS.forEach { collector.emit(it) }
            }

            coEvery {
                auditCollection.find()
            } returns mockFindFlow

            //When
            val result = auditRepository.getTaskAuditLogById(MockAuditLog.AUDIT_LOG.itemId)

            //Then
            assertThat(result).containsExactlyElementsIn(MockAuditLog.AUDIT_LOGS)
        }
    }

    @Test
    fun `getTaskAuditLogById should throw Exception when DB operation fails`() {
        runTest {
            // Given
            coEvery {
                auditCollection.find(any<Bson>())
            } throws MongoException("Can't get AuditLogs")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                auditRepository.getTaskAuditLogById(MockAuditLog.AUDIT_LOG.itemId)
            }
        }
    }
    //endregion

    // region getAuditLogs
    @Test
    fun `getAuditLogs should return list of AuditLogs`() {
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<AuditLog>>()

            coEvery { auditCollection.find(any<Bson>()) } returns mockFindFlow
            coEvery { mockFindFlow.collect(any()) } coAnswers {
                val collector = arg<FlowCollector<AuditLog>>(0)
                collector.emit(MockAuditLog.AUDIT_LOG)
            }

            coEvery {
                auditCollection.find()
            } returns mockFindFlow

            //When
            val result = auditRepository.getAuditLogs()

            //Then
            assertThat(result).containsExactlyElementsIn(listOf(MockAuditLog.AUDIT_LOG))
        }
    }

    @Test
    fun `getAuditLogs should return empty list of AuditLogs when DB is empty`() {
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<AuditLog>>()

            coEvery { auditCollection.find(any<Bson>()) } returns mockFindFlow
            coEvery { mockFindFlow.collect(any()) } coAnswers {
            }

            coEvery {
                auditCollection.find()
            } returns mockFindFlow

            //When
            val result = auditRepository.getAuditLogs()

            //Then
            assertThat(result).containsExactlyElementsIn(emptyList<AuditLog>())
        }
    }

    @Test
    fun `getAuditLogs should throw Exception when DB operation fails`() {
        runTest {
            // Given
            coEvery {
                auditCollection.find()
            } throws MongoException("Can't get AuditLogs")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                auditRepository.getAuditLogs()
            }
        }
    }
    //endregion

}