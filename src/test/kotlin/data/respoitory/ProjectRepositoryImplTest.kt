package data.respoitory

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.example.data.respoitory.ProjectRepositoryImpl
import org.example.data.storge.audit.AuditDataSource
import org.example.data.storge.project.ProjectDataSource
import org.example.domain.model.EiffelFlowException
import org.example.domain.model.entities.Project
import org.example.domain.model.entities.State
import org.example.domain.repository.ProjectRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

//todo change all of the test
class ProjectRepositoryImplTest {

    private lateinit var projectRepository: ProjectRepository
    private val projectDataSource: ProjectDataSource = mockk()
    private val auditDataSource: AuditDataSource = mockk()

    @BeforeEach
    fun setUp() {
        projectRepository = ProjectRepositoryImpl(projectDataSource, auditDataSource)
    }

    @Test
    fun `createProject should return the created project`() {
        val project = Project(
            projectName = "Test",
            projectDescription = "Test",
            createdAt = LocalDateTime(2023, 1, 1, 12, 0),
            adminId = UUID.randomUUID(),
            states = listOf(
                State(
                    stateId = UUID.randomUUID(),
                    name = "To"
                )
            )
        )

        try {
            projectRepository.createProject(project)
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
        val projectId = UUID.randomUUID()

        try {
            projectRepository.deleteProject(projectId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of empty list of Projects when there is no project in data source`() {
        //Given
        every { projectDataSource.getProjects() } returns Result.success(emptyList())

        // When / Then
        try {
            val result = projectRepository.getProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of Projects when at least one project exists in data source`() {
        //Given
        every { projectDataSource.getProjects() } returns Result.success(listOf(PROJECT))

        // When / Then
        try {
            val result = projectRepository.getProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of ElementNotFoundException when project doesn't exists in data source`() {
        //Given
        val exception = EiffelFlowException.ElementNotFoundException("Project not found")
        every { projectDataSource.getProjects() } returns Result.failure(exception)

        // When / Then
        try {
            val result = projectRepository.getProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of Project when the given Id match project record exists in data source`() {
        //Given
        every { projectDataSource.getProjectById(PROJECT.projectId) } returns Result.success(PROJECT)

        // When / Then
        try {
            val result = projectRepository.getProjectById(PROJECT.projectId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of ElementNotFoundException when searching for project doesn't exists in data source`() {
        val exception = EiffelFlowException.ElementNotFoundException("Project not found")
        every { projectDataSource.getProjectById(UUID.randomUUID()) } returns Result.failure(exception)

        // When / Then
        try {
            val result =  projectRepository.getProjectById(UUID.randomUUID())
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    companion object {
        private val PROJECT = Project(
            projectId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748"),
            projectName = "Project1",
            projectDescription = "Description1",
            createdAt = LocalDateTime.parse("1999-08-07T22:22:22"),
            adminId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5741"),
            states = emptyList()
        )

        private const val CSV_STRING_LINE =
            "02ad4499-5d4c-4450-8fd1-8294f1bb5748,Project1,Description1,1999-08-07T22:22:22,02ad4499-5d4c-4450-8fd1-8294f1bb5741"
    }
}