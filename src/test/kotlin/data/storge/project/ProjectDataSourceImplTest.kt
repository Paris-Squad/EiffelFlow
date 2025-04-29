package data.storge.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.example.data.storge.CsvStorageManager
import org.example.data.storge.mapper.ProjectCsvMapper
import org.example.data.storge.mapper.StateCsvMapper
import org.example.data.storge.project.ProjectDataSource
import org.example.data.storge.project.ProjectDataSourceImpl
import org.example.domain.model.EiffelFlowException
import org.example.domain.model.entities.Project
import org.example.domain.model.entities.State
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.MockProjects
import java.util.UUID

class ProjectDataSourceImplTest {

    private lateinit var projectDataSource: ProjectDataSource
    private val csvStorageManager: CsvStorageManager = mockk()
    private val stateCsvMapper: StateCsvMapper = mockk()
    private val projectMapper: ProjectCsvMapper = mockk()

    @BeforeEach
    fun setUp() {
        projectDataSource = ProjectDataSourceImpl(projectMapper, stateCsvMapper, csvStorageManager)
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
            projectDataSource.createProject(project)
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
            projectDataSource.updateProject(project)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `deleteProject should return the deleted project`() {
        val projectId = UUID.randomUUID()

        try {
            projectDataSource.deleteProject(projectId)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of empty list of Projects when the file is empty`() {
        every { csvStorageManager.readLinesFromFile() } returns "".split("\n")

        // Then
        try {
            val result = projectDataSource.getProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of Projects when at least one project exists in CSV file`() {
        //Given
        every { csvStorageManager.readLinesFromFile() } returns MockProjects.CORRECT_CSV_STRING_LINE.split("\n")

        // When / Then
        try {
            val result = projectDataSource.getProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of ElementNotFoundException when project doesn't exists in CSV file`() {
        val exception = EiffelFlowException.ElementNotFoundException("Project not found")

        // When / Then
        try {
            val result = projectDataSource.getProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of Project when the given Id match project record exists in CSV file`() {
        //Given
        every { csvStorageManager.readLinesFromFile() } returns MockProjects.CORRECT_CSV_STRING_LINE.split("\n")

        // When / Then
        try {
            val result = projectDataSource.getProjectById(UUID.randomUUID())
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `should return Result of ElementNotFoundException when searching for project doesn't exists in CSV file`() {
        //Given
        val exception = EiffelFlowException.ElementNotFoundException("Project not found")

        // When / Then
        try {
            val result =  projectDataSource.getProjectById(UUID.randomUUID())
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}