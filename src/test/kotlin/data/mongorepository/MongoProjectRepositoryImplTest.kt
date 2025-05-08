//package data.mongorepository
//
//import com.google.common.truth.Truth.assertThat
//import com.mongodb.client.model.Filters.eq
//import com.mongodb.client.model.FindOneAndUpdateOptions
//import com.mongodb.kotlin.client.coroutine.MongoCollection
//import com.mongodb.kotlin.client.coroutine.MongoDatabase
//import io.mockk.coEvery
//import io.mockk.every
//import io.mockk.mockk
//import kotlinx.coroutines.flow.firstOrNull
//import kotlinx.coroutines.flow.toList
//import kotlinx.coroutines.test.runTest
//import org.bson.Document
//import org.example.data.storage.SessionManger
//import org.example.domain.model.Project
//import org.example.domain.repository.AuditRepository
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import utils.MockAuditLog
//import utils.ProjectsMock
//import utils.UserMock
//
//class MongoProjectRepositoryImplTest {
//
//    private val sessionManger: SessionManger = mockk(relaxed = true)
//    private val auditRepository: AuditRepository = mockk(relaxed = true)
//    private val mongoDatabase: MongoDatabase = mockk()
//    private val projectCollection: MongoCollection<Project> = mockk()
//    private lateinit var repository: MongoProjectRepositoryImpl
//
//    @BeforeEach
//    fun setUp() {
//        every {
//            mongoDatabase.getCollection<Project>(any())
//        } returns projectCollection
//        repository = MongoProjectRepositoryImpl(
//            database = mongoDatabase,
//            auditRepository = auditRepository
//        )
//    }
//
//    @Test
//    fun `createProject should returns the project when projectRepository and auditRepository succeed`() {
//        runTest {
//            try {
//                // Given
//                every { sessionManger.getUser() } returns UserMock.adminUser
//                coEvery {
//                    projectCollection.findOneAndUpdate(
//                        any<Document>(),
//                        any<Document>(),
//                        any<FindOneAndUpdateOptions>()
//                    )
//                } returns ProjectsMock.CORRECT_PROJECT
//
//                coEvery {
//                    auditRepository.createAuditLog(any())
//                } returns MockAuditLog.AUDIT_LOG
//
//
//                //When
//                val result = repository.createProject(ProjectsMock.CORRECT_PROJECT)
//
//                //Then
//                assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
//            } catch (e: NotImplementedError) {
//                assertThat(e.message).contains("Not yet implemented")
//            }
//        }
//    }
//
//    @Test
//    fun `updateProject should return success if the project is updated`() {
//        runTest {
//            try {
//                // Given
//                coEvery {
//                    projectCollection.findOneAndReplace(any(), any())
//                } returns ProjectsMock.updatedProject
//
//                //When
//                val result = repository.updateProject(
//                    project = ProjectsMock.updatedProject,
//                    oldProject = ProjectsMock.CORRECT_PROJECT,
//                    changedField = "projectDescription"
//                )
//
//                //Then
//                assertThat(result).isEqualTo(ProjectsMock.updatedProject)
//            } catch (e: NotImplementedError) {
//                assertThat(e.message).contains("Not yet implemented")
//            }
//        }
//    }
//
//    @Test
//    fun `deleteProject should return the deleted project`() {
//        runTest {
//            try {
//                // Given
//
//                coEvery {
//                    projectCollection.findOneAndDelete(any())
//                } returns ProjectsMock.CORRECT_PROJECT
//
//                //When
//                val result = repository.deleteProject(ProjectsMock.CORRECT_PROJECT.projectId)
//
//                //Then
//                assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
//            } catch (e: NotImplementedError) {
//                assertThat(e.message).contains("Not yet implemented")
//            }
//        }
//    }
//
//    @Test
//    fun `should return List of Projects when there are projects exist in CSV file`() {
//        runTest {
//            try {
//                // Given
//                coEvery {
//                    projectCollection.find().toList()
//                } returns listOf(ProjectsMock.CORRECT_PROJECT)
//
//                //When
//                val result = repository.getProjects()
//
//                //Then
//                assertThat(result).containsExactlyElementsIn(listOf(ProjectsMock.CORRECT_PROJECT))
//            } catch (e: NotImplementedError) {
//                assertThat(e.message).contains("Not yet implemented")
//            }
//        }
//    }
//
//    @Test
//    fun `should return Project when the given Id match project record exists in CSV file`() {
//        runTest {
//            try {
//                // Given
//                val query = eq("projectId", ProjectsMock.CORRECT_PROJECT.projectId)
//                coEvery {
//                    projectCollection.find(query).firstOrNull()
//                } returns ProjectsMock.CORRECT_PROJECT
//
//                //When
//                val result = repository.getProjectById(ProjectsMock.CORRECT_PROJECT.projectId)
//
//                //Then
//                assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
//            } catch (e: NotImplementedError) {
//                assertThat(e.message).contains("Not yet implemented")
//            }
//        }
//    }
//
//}