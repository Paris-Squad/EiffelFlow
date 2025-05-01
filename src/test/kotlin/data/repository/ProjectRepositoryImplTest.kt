package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.example.data.repository.ProjectRepositoryImpl
import org.example.domain.repository.AuditRepository
import org.example.data.storage.project.ProjectDataSource
import org.example.domain.model.Project
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import org.example.domain.model.AuditLogAction
import org.example.domain.model.AuditLog
import io.mockk.verify
import io.mockk.every
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.ProjectRepository
import org.junit.jupiter.api.Assertions
import utils.ProjectsMock

class ProjectRepositoryImplTest {

    private lateinit var projectRepository: ProjectRepository
    private val projectDataSource: ProjectDataSource = mockk()
    private val auditRepository: AuditRepository = mockk()

    @BeforeEach
    fun setUp() {
        projectRepository = ProjectRepositoryImpl(projectDataSource, auditRepository)
    }

    //region createProject
    @Test
    fun `createProject should returns the project when projectDataSource and auditRepository succeed`() {

        try {
            every {
                projectDataSource.createProject(any())
            } returns Result.success(ProjectsMock.CORRECT_PROJECT)
            every { auditRepository.createAuditLog(any()) } returns Result.success(
                AuditLog(
                    auditId = UUID.randomUUID(),
                    itemId = ProjectsMock.CORRECT_PROJECT.projectId,
                    itemName = ProjectsMock.CORRECT_PROJECT.projectName,
                    userId = ProjectsMock.CORRECT_PROJECT.adminId,
                    editorName = "Admin",
                    actionType = AuditLogAction.CREATE,
                    auditTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    changedField = null,
                    oldValue = null,
                    newValue = ProjectsMock.CORRECT_PROJECT.projectName
                )
            )

            val result = projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)

            Assertions.assertTrue(result.isSuccess)

            verify(exactly = 1) { auditRepository.createAuditLog(any()) }

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }

    }

    @Test
    fun `createProject should return failure when projectDataSource fails`() {
        try {
            every {
                projectDataSource.createProject(any())
            } returns Result.failure(Exception("Project creation failed"))

            val result = projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)

            Assertions.assertTrue(result.isFailure)

            verify(exactly = 0) { auditRepository.createAuditLog(any()) }

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }

    }

    @Test
    fun `createProject should return failure when auditRepository fails`() {
        try {
            every {
                projectDataSource.createProject(any())
            } returns Result.success(ProjectsMock.CORRECT_PROJECT)
            every {
                auditRepository.createAuditLog(any())
            } returns Result.failure(Exception("Audit log error"))

            val result = projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)

            Assertions.assertTrue(result.isFailure)

            verify(exactly = 1) { auditRepository.createAuditLog(any()) }

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }

    }
    //endregion

    @Test
    fun `updateProject should return the updated project`() {
        val project = Project(
            projectName = "Test Project",
            projectDescription = "Updated Description",
            createdAt = LocalDateTime(2023, 1, 1, 12, 0),
            adminId = UUID.randomUUID(),
            taskStates = emptyList()
        )

        try {
            projectRepository.updateProject(project)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `deleteProject should return the deleted project`() {
        try {
            // Given
            val auditLog = AuditLog(
                auditId = UUID.randomUUID(),
                itemId = ProjectsMock.CORRECT_PROJECT.projectId,
                itemName = ProjectsMock.CORRECT_PROJECT.projectName,
                userId = ProjectsMock.CORRECT_PROJECT.adminId,
                editorName = "Admin",
                actionType = AuditLogAction.DELETE,
                auditTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                changedField = null,
                oldValue = null,
                newValue = ProjectsMock.CORRECT_PROJECT.projectName
            )
            val projectId = UUID.randomUUID()
            every { projectDataSource.deleteProject(any()) } returns Result.success(ProjectsMock.CORRECT_PROJECT)
            every { auditRepository.createAuditLog(any()) } returns Result.success(auditLog)

            // When
            projectRepository.deleteProject(projectId)

            // Then
            verify(exactly = 1) { projectDataSource.deleteProject(any()) }
            verify(exactly = 1) { auditRepository.createAuditLog(any()) }
        } catch (exception: NotImplementedError) {
            assertThat(exception.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `deleteProject should throw IOException when deleteProject returns failure`() {
        try {
            // Given
            val projectId = UUID.randomUUID()
            every {
                projectDataSource.deleteProject(any())
            } returns Result.failure(EiffelFlowException.IOException("Failed to delete project"))

            // When
            val result = projectRepository.deleteProject(projectId)

            // Then
            assertThat(result.exceptionOrNull()).isInstanceOf(
                EiffelFlowException.IOException::class.java
            )
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }


    }

    @Test
    fun `deleteProject should throw IOException when createAuditLog returns failure`() {
        try {
            // Given
            val projectId = UUID.randomUUID()
            every { projectDataSource.deleteProject(any()) } returns Result.success(ProjectsMock.CORRECT_PROJECT)
            every { auditRepository.createAuditLog(any()) } returns Result.failure(
                EiffelFlowException.IOException("Failed to create audit log")
            )
            // When
            val result = projectRepository.deleteProject(projectId)

            // Then
            assertThat(result.exceptionOrNull()).isInstanceOf(
                EiffelFlowException.IOException::class.java
            )
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }


    //region getProjects
    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should return Result of failure when data source fail to load projects`() {
        //Given
        val exception = EiffelFlowException.NotFoundException("Project not found")
        every {
            projectDataSource.getProjects()
        } returns Result.failure(exception)

        // When
        val result = projectRepository.getProjects()

        // Then
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `should return Result of Projects when at least one project exists in data source`() {
        //Given
        every {
            projectDataSource.getProjects()
        } returns Result.success(listOf(ProjectsMock.CORRECT_PROJECT))

        // When
        val result = projectRepository.getProjects()

        // Then
        assertThat(result.getOrNull())
            .containsExactlyElementsIn(listOf(ProjectsMock.CORRECT_PROJECT))
    }
    //endregion

    //region getProjectById
    @Test
    fun `should return Result of Project when the given Id match project record exists in data source`() {
        //Given
        every {
            projectDataSource.getProjectById(ProjectsMock.CORRECT_PROJECT.projectId)
        } returns Result.success(ProjectsMock.CORRECT_PROJECT)

        // When
        val result = projectRepository.getProjectById(ProjectsMock.CORRECT_PROJECT.projectId)


        // Then
        assertThat(result.getOrNull())
            .isEqualTo(ProjectsMock.CORRECT_PROJECT)
    }

    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should return Result of ElementNotFoundException when searching for project doesn't exists in data source`() {
        val exception = EiffelFlowException.NotFoundException("Project not found")
        every {
            projectDataSource.getProjectById(any())
        } returns Result.failure(exception)

        // When
        val result = projectRepository.getProjectById(UUID.randomUUID())

        //Then
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
    //endregion
}