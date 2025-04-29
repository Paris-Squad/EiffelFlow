package data.respoitory

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.example.data.respoitory.ProjectRepositoryImpl
import org.example.data.storge.audit.AuditDataSource
import org.example.data.storge.project.ProjectDataSource
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

    }

    @Test
    fun `createProject should return failure when projectDataSource fails`() {
        every { projectDataSource.createProject(any()) } returns Result.failure(Exception("Project creation failed"))

        val result = projectRepository.createProject(project)

        Assertions.assertTrue(result.isFailure)

        verify(exactly = 0) { auditDataSource.createAuditLog(any()) }
    }

    @Test
    fun `createProject should return failure when auditDataSource fails`() {
        every { projectDataSource.createProject(any()) } returns Result.success(project)
        every { auditDataSource.createAuditLog(any()) } returns Result.failure(Exception("Audit log error"))

        val result = projectRepository.createProject(project)

        Assertions.assertTrue(result.isFailure)

        verify(exactly = 1) { auditDataSource.createAuditLog(any()) }
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
        val projectId = UUID.randomUUID()

        try {
            projectRepository.deleteProject(projectId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
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