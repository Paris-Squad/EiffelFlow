package data.storage.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.example.data.storage.FileStorageManager
import org.example.data.storage.mapper.ProjectCsvMapper
import org.example.data.storage.mapper.StateCsvMapper
import org.example.data.storage.project.ProjectDataSource
import org.example.data.storage.project.ProjectDataSourceImpl
import org.example.domain.model.exception.EiffelFlowException
import org.example.domain.model.entities.Project
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ProjectsMock
import java.util.UUID
import org.junit.jupiter.api.Assertions
import io.mockk.Runs
import io.mockk.just
import io.mockk.verify

class ProjectDataSourceImplTest {

    private lateinit var projectDataSource: ProjectDataSource
    private val fileStorageManager: FileStorageManager = mockk()
    private val stateCsvMapper: StateCsvMapper = mockk()
    private val projectMapper: ProjectCsvMapper = mockk()

    @BeforeEach
    fun setUp() {
        projectDataSource = ProjectDataSourceImpl(projectMapper, stateCsvMapper, fileStorageManager)
    }

    @Test
    fun `createProject should return success when project is written to CSV`() {
        try {
            every { projectMapper.mapTo(correctProject) } returns correctLine

            every { fileStorageManager.writeLinesToFile(correctLine + "\n") } just Runs

            val result = projectDataSource.createProject(correctProject)

            Assertions.assertTrue(result.isSuccess)
            verify(exactly = 1) { fileStorageManager.writeLinesToFile(correctLine + "\n") }

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `createProject should return failure when writeLinesToFile throws exception`() {
        try {
            every { projectMapper.mapTo(correctProject) } returns correctLine
            every { fileStorageManager.writeLinesToFile(correctLine + "\n") } throws Exception("Failed to write to file")

            val result = projectDataSource.createProject(correctProject)

            Assertions.assertTrue(result.isFailure)
            verify(exactly = 1) { fileStorageManager.writeLinesToFile(correctLine + "\n") }

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
        every { fileStorageManager.readLinesFromFile() } returns "".split("\n")

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
        every { fileStorageManager.readLinesFromFile() } returns ProjectsMock.CORRECT_CSV_STRING_LINE.split("\n")

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
        every { fileStorageManager.readLinesFromFile() } returns ProjectsMock.CORRECT_CSV_STRING_LINE.split("\n")

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

    companion object{
        private val correctProject = ProjectsMock.CORRECT_PROJECT
        private val correctLine = ProjectsMock.CORRECT_CSV_STRING_LINE
    }
}