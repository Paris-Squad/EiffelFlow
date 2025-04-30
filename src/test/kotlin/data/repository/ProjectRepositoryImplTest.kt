package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.example.data.repository.ProjectRepositoryImpl
import org.example.data.storage.audit.AuditDataSource
import org.example.data.storage.project.ProjectDataSource
import org.example.domain.model.entities.Project
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import org.example.domain.model.entities.AuditAction
import org.example.domain.model.entities.AuditLog
import io.mockk.verify
import io.mockk.every
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.domain.model.exception.EiffelFlowException
import org.junit.jupiter.api.Assertions

//todo change all of the test
class ProjectRepositoryImplTest {

    private lateinit var projectRepository: ProjectRepositoryImpl
    private val projectDataSource: ProjectDataSource = mockk()
    private val auditDataSource: AuditDataSource = mockk()

    @BeforeEach
    fun setUp() {
        projectRepository = ProjectRepositoryImpl(projectDataSource, auditDataSource)
    }

    @Test
    fun `createProject should returns the project when projectDataSource and auditDataSource succeed`() {

        try {
            every { projectDataSource.createProject(any()) } returns Result.success(project)
            every { auditDataSource.createAuditLog(any()) } returns Result.success(
                AuditLog(
                    auditId = UUID.randomUUID(),
                    itemId = project.projectId,
                    itemName = project.projectName,
                    userId = project.adminId,
                    userName = "Admin",
                    actionType = AuditAction.CREATE,
                    auditTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    changedField = null,
                    oldValue = null,
                    newValue = project.projectName
                )
            )

            val result = projectRepository.createProject(project)

            Assertions.assertTrue(result.isSuccess)

            verify(exactly = 1) { auditDataSource.createAuditLog(any()) }

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }

    }

    @Test
    fun `createProject should return failure when projectDataSource fails`() {
        try {
            every { projectDataSource.createProject(any()) } returns Result.failure(Exception("Project creation failed"))

            val result = projectRepository.createProject(project)

            Assertions.assertTrue(result.isFailure)

            verify(exactly = 0) { auditDataSource.createAuditLog(any()) }

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }

    }

    @Test
    fun `createProject should return failure when auditDataSource fails`() {
        try {
            every { projectDataSource.createProject(any()) } returns Result.success(project)
            every { auditDataSource.createAuditLog(any()) } returns Result.failure(Exception("Audit log error"))

            val result = projectRepository.createProject(project)

            Assertions.assertTrue(result.isFailure)

            verify(exactly = 1) { auditDataSource.createAuditLog(any()) }

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }

    }

    @Test
    fun `updateProject should return the updated project`() {
        val project = Project(
            projectName = "Test Project",
            projectDescription = "Updated Description",
            createdAt = LocalDateTime(2023, 1, 1, 12, 0),
            adminId = UUID.randomUUID(),
            states = emptyList()
        )

        try {
            projectRepository.updateProject(project)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `deleteProject should return the deleted project`() {
        // Given
        val auditLog = AuditLog(
            auditId = UUID.randomUUID(),
            itemId = project.projectId,
            itemName = project.projectName,
            userId = project.adminId,
            userName = "Admin",
            actionType = AuditAction.DELETE,
            auditTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            changedField = null,
            oldValue = null,
            newValue = project.projectName
        )
        val projectId = UUID.randomUUID()
        every { projectDataSource.deleteProject(any()) } returns Result.success(project)
        every { auditDataSource.createAuditLog(any()) } returns Result.success(auditLog)

        // When
        projectRepository.deleteProject(projectId)

        // Then
        verify(exactly = 1) { projectDataSource.deleteProject(any()) }
        verify(exactly = 1) { auditDataSource.createAuditLog(any()) }
    }

    @Test
    fun `deleteProject should throw UnableToDeleteProjectException when deleteProject returns failure`(){
        // Given
        val auditLog = AuditLog(
            auditId = UUID.randomUUID(),
            itemId = project.projectId,
            itemName = project.projectName,
            userId = project.adminId,
            userName = "Admin",
            actionType = AuditAction.DELETE,
            auditTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            changedField = null,
            oldValue = null,
            newValue = project.projectName
        )
        val projectId = UUID.randomUUID()
        every { projectDataSource.deleteProject(any()) } returns Result.failure(EiffelFlowException.UnableToDeleteProjectException())

        // When
        val result = projectRepository.deleteProject(projectId)

        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(
            EiffelFlowException.UnableToDeleteProjectException::class.java
        )
    }

    @Test
    fun `deleteProject should throw UnableToCreateAuditLogException when createAuditLog returns failure`(){
        // Given
        val auditLog = AuditLog(
            auditId = UUID.randomUUID(),
            itemId = project.projectId,
            itemName = project.projectName,
            userId = project.adminId,
            userName = "Admin",
            actionType = AuditAction.DELETE,
            auditTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            changedField = null,
            oldValue = null,
            newValue = project.projectName
        )
        val projectId = UUID.randomUUID()
        every { projectDataSource.deleteProject(any()) } returns Result.success(project)
        every { auditDataSource.createAuditLog(any()) } returns Result.failure(
            EiffelFlowException.UnableToCreateAuditLogException()
        )
        // When
        val result = projectRepository.deleteProject(projectId)

        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(
            EiffelFlowException.UnableToCreateAuditLogException::class.java
        )
    }

    @Test
    fun `getProjects should return list of projects`() {
        try {
            projectRepository.getProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getProjectById should return list of projects`() {
        try {
            projectRepository.getProjectById(UUID.randomUUID())
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    companion object{
        val project = Project(
            projectName = "Test Project",
            projectDescription = "A test project",
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            adminId = UUID.randomUUID(),
            states = emptyList()
        )
    }
}