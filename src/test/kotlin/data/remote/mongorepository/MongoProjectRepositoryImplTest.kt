package data.remote.mongorepository

import com.google.common.truth.Truth.assertThat
import com.mongodb.MongoException
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import data.mongorepository.MongoProjectRepositoryImpl
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bson.conversions.Bson
import org.example.data.remote.MongoCollections
import org.example.data.remote.mapper.ProjectMapper
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.AuditLog
import org.example.domain.model.Project
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.ProjectRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ProjectsMock
import utils.UserMock
import java.util.UUID

class MongoProjectRepositoryImplTest {

    private val projectMapper : ProjectMapper= mockk(relaxed = true)
    private val sessionManger: SessionManger = mockk(relaxed = true)
    private lateinit var projectsCollection: MongoCollection<Project>
    private lateinit var auditRepository: AuditRepository
    private lateinit var projectRepository: ProjectRepository

    @BeforeEach
    fun setup() {
        projectsCollection = mockk(relaxed = true)
        auditRepository = mockk(relaxed = true)

        val mockDatabase = mockk<MongoDatabase>()
        every {
            mockDatabase.getCollection<Project>(MongoCollections.PROJECTS)
        } returns projectsCollection

        every { sessionManger.getUser() } returns UserMock.adminUser

        projectRepository = MongoProjectRepositoryImpl(
           database =  mockDatabase,
            projectMapper = projectMapper
        )
    }

    //region createProject
    @Test
    fun `createProject should insert project if not exists`() = runTest {
        //Given
        val mockFindFlow = mockk<FindFlow<Project>>()

        coEvery { projectsCollection.find(any<Bson>()) } returns mockFindFlow

        coEvery { mockFindFlow.collect(any()) } coAnswers {
        }
        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()
        coEvery {
            projectsCollection.insertOne(eq(ProjectsMock.CORRECT_PROJECT), any())
        } returns mockk()

        //When
        val result = projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)

        //Then
        assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
    }

    @Test
    fun `createProject should throw if project already exists`() = runTest {
        //Given
        val mockFindFlow = mockk<FindFlow<Project>>()
        coEvery { projectsCollection.find(any<Bson>()) } returns mockFindFlow
        coEvery { mockFindFlow.collect(any()) } coAnswers {
            val collector = arg<FlowCollector<Project>>(0)
            collector.emit(ProjectsMock.CORRECT_PROJECT)
        }
        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()

        //When /Then
        Assertions.assertThrows(EiffelFlowException.IOException::class.java) {
            runBlocking { projectRepository.createProject(ProjectsMock.CORRECT_PROJECT) }
        }
    }

    @Test
    fun `createProject should throw Exception when audit log creation fails`() = runTest {
        //Given
        val mockFindFlow = mockk<FindFlow<Project>>()

        coEvery { projectsCollection.find(any<Bson>()) } returns mockFindFlow

        coEvery { mockFindFlow.collect(any()) } coAnswers {
        }
        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()
        coEvery {
            projectsCollection.insertOne(eq(ProjectsMock.CORRECT_PROJECT), any())
        } throws EiffelFlowException.IOException("Custom exception")

        //When then
        assertThrows<EiffelFlowException.IOException> {
            projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)
        }

    }

    @Test
    fun `createProject should throw Exception when write to mongodb fails`() = runTest {
        //Given
        val mockFindFlow = mockk<FindFlow<Project>>()

        coEvery { projectsCollection.find(any<Bson>()) } returns mockFindFlow

        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()

        coEvery {
            projectsCollection.insertOne(eq(ProjectsMock.CORRECT_PROJECT), any())
        } returns mockk()

        assertThrows<EiffelFlowException.IOException> {
            projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)
        }
    }
    //endregion

    //region updateProject
    @Test
    fun `updateProject should update correct if the item exists`() = runTest {
        //Given
        coEvery {
            projectsCollection.findOneAndUpdate(
                any<Bson>(),
                any<Bson>(),
                any()
            )
        } returns ProjectsMock.CORRECT_PROJECT

        // When
        val result = projectRepository.updateProject(
            project = ProjectsMock.updatedProject,
            oldProject = ProjectsMock.CORRECT_PROJECT,
            changedField = "name"
        )

        assertThat(result).isEqualTo(ProjectsMock.updatedProject)
    }

    @Test
    fun `updateProject should throw Exception when project is not found`() {
        runTest {
            // Given
            coEvery {
                projectsCollection.findOneAndUpdate(
                    any<Bson>(),
                    any<Bson>(),
                    any()
                )
            } returns null

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                projectRepository.updateProject(
                    project = ProjectsMock.updatedProject,
                    oldProject = ProjectsMock.CORRECT_PROJECT,
                    changedField = "description"
                )
            }
        }
    }

    @Test
    fun `updateProject should return Exception when writing to DB fails`() {
        runTest {
            // Given
            coEvery {
                projectsCollection.findOneAndUpdate(
                    any<Bson>(),
                    any<List<Bson>>(),
                    any()
                )
            } throws MongoException("Can't update this project")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                projectRepository.updateProject(
                    project = ProjectsMock.updatedProject,
                    oldProject = ProjectsMock.CORRECT_PROJECT,
                    changedField = "state"
                )
            }
        }
    }

    @Test
    fun `updateProject should throw Exception when audit log creation fails`() {
        runTest {
            // Given
            coEvery {
                auditRepository.createAuditLog(any())
            } throws Throwable("Can't Update Project")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                projectRepository.updateProject(
                    project = ProjectsMock.updatedProject,
                    oldProject = ProjectsMock.CORRECT_PROJECT,
                    changedField = "role"
                )
            }
        }
    }
    //endregion

    //region deleteProject
    @Test
    fun `deleteProject should return the deleted project on success`() = runTest {
        coEvery {
            projectsCollection.findOneAndDelete(
                any<Bson>(),
                any()
            )
        } returns ProjectsMock.updatedProject
        coEvery {
            auditRepository.createAuditLog(any())
        } returns mockk<AuditLog>()

        // When
        val result = projectRepository.deleteProject(ProjectsMock.updatedProject.projectId)

        // Then
        assertThat(result).isEqualTo(ProjectsMock.updatedProject)
    }

    @Test
    fun `deleteProject should throw Exception when project is not found`() {
        runTest {
            //Given
            coEvery {
                projectsCollection.findOneAndDelete(
                    any<Bson>(),
                    any()
                )
            } returns null

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                projectRepository.deleteProject(UUID.randomUUID())
            }
        }
    }

    @Test
    fun `deleteProject should throw Exception when writing to DB fails`() {
        runTest {
            // Given
            coEvery {
                projectsCollection.findOneAndDelete(
                    any<Bson>(),
                    any()
                )
            } throws MongoException("Can't delete this project")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                projectRepository.deleteProject(ProjectsMock.CORRECT_PROJECT.projectId)
            }
        }
    }

    @Test
    fun `deleteProject should throw Exception when audit log creation fails`() {
        runTest {
            // Given
            coEvery {
                auditRepository.createAuditLog(any())
            } throws Throwable("Can't Delete Project")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                projectRepository.deleteProject(ProjectsMock.CORRECT_PROJECT.projectId)
            }
        }
    }
    //endregion

    //region getProjectById
    @Test
    fun `getProjectById should return the project on success`() {
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<Project>>()

            coEvery { projectsCollection.find(any<Bson>()) } returns mockFindFlow

            coEvery { mockFindFlow.collect(any()) } coAnswers {
                val collector = arg<FlowCollector<Project>>(0)
                collector.emit(ProjectsMock.CORRECT_PROJECT)
            }

            coEvery {
                projectsCollection.find()
            } returns mockFindFlow

            //When
            val result = projectRepository.getProjectById(ProjectsMock.CORRECT_PROJECT.projectId)

            //Then
            assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
        }
    }

    @Test
    fun `getProjectById should throw Exception when user is not admin`() = runTest {
        // Given
        every { sessionManger.getUser() } returns UserMock.validUser

        // When/Then
        assertThrows<EiffelFlowException.AuthorizationException> {
            projectRepository.getProjectById(ProjectsMock.CORRECT_PROJECT.projectId)
        }
    }

    @Test
    fun `getProjectById should throw Exception when project is not found`() {
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<Project>>()

            coEvery { projectsCollection.find(any<Bson>()) } returns mockFindFlow

            coEvery { mockFindFlow.collect(any()) } coAnswers {

            }

            coEvery {
                projectsCollection.find()
            } returns mockFindFlow

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                projectRepository.getProjectById(UUID.randomUUID())
            }
        }
    }

    @Test
    fun `getProjectById should throw Exception when DB operation fails`() {
        runTest {
            // Given
            coEvery {
                projectsCollection.find()
            } throws MongoException("Can't get Project")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                projectRepository.getProjectById(ProjectsMock.CORRECT_PROJECT.projectId)
            }
        }
    }
    //endregion

    // region getProjects
    @Test
    fun `getProjects should return list of Projects`() {
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<Project>>()

            coEvery { projectsCollection.find(any<Bson>()) } returns mockFindFlow
            coEvery { mockFindFlow.collect(any()) } coAnswers {
                val collector = arg<FlowCollector<Project>>(0)
                collector.emit(ProjectsMock.CORRECT_PROJECT)
            }

            coEvery {
                projectsCollection.find()
            } returns mockFindFlow

            //When
            val result = projectRepository.getProjects()

            //Then
            assertThat(result).containsExactlyElementsIn(listOf(ProjectsMock.CORRECT_PROJECT))
        }
    }

    @Test
    fun `getProjects should throw Exception when user is not admin`() = runTest {
        // Given
        every { sessionManger.getUser() } returns UserMock.validUser

        // When/Then
        assertThrows<EiffelFlowException.AuthorizationException> {
            projectRepository.getProjects()
        }
    }

    @Test
    fun `getProjects should return empty list of Projects when DB is empty`() {
        runTest {
            // Given
            val mockFindFlow = mockk<FindFlow<Project>>()

            coEvery { projectsCollection.find(any<Bson>()) } returns mockFindFlow
            coEvery { mockFindFlow.collect(any()) } coAnswers {
            }

            coEvery {
                projectsCollection.find()
            } returns mockFindFlow

            //When
            val result = projectRepository.getProjects()

            //Then
            assertThat(result).containsExactlyElementsIn(emptyList<Project>())
        }
    }

    @Test
    fun `getProjects should throw Exception when DB operation fails`() {
        runTest {
            // Given
            coEvery {
                projectsCollection.find()
            } throws MongoException("Can't get Project")

            // When & Then
            assertThrows<EiffelFlowException.IOException> {
                projectRepository.getProjects()
            }
        }
    }
    //endregion

}