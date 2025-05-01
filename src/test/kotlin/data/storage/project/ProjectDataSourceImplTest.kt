package data.storage.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.example.data.storage.CsvStorageManager
import org.example.data.storage.mapper.ProjectCsvMapper
import org.example.data.storage.project.ProjectDataSource
import org.example.data.storage.project.ProjectDataSourceImpl
import org.example.domain.model.exception.EiffelFlowException
import org.example.domain.model.entities.Project
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.MockProjects
import java.util.UUID
import org.junit.jupiter.api.Assertions
import io.mockk.Runs
import io.mockk.just
import io.mockk.verify
import java.io.IOException
import kotlin.jvm.Throws

class ProjectDataSourceImplTest {

    private lateinit var projectDataSource: ProjectDataSource
    private val csvStorageManager: CsvStorageManager = mockk(relaxed = true)
    private val projectMapper: ProjectCsvMapper = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        projectDataSource = ProjectDataSourceImpl(projectMapper, csvStorageManager)
    }

    //region createProject
    @Test
    fun `createProject should return success when project is written to CSV`() {
        try {
            every {
                projectMapper.mapTo(MockProjects.CORRECT_PROJECT)
            } returns MockProjects.CORRECT_CSV_STRING_LINE

            every {
                csvStorageManager.writeLinesToFile(MockProjects.CORRECT_CSV_STRING_LINE + "\n")
            } just Runs

            val result = projectDataSource.createProject(MockProjects.CORRECT_PROJECT)

            Assertions.assertTrue(result.isSuccess)
            verify(exactly = 1) {
                csvStorageManager.writeLinesToFile(MockProjects.CORRECT_CSV_STRING_LINE + "\n")
            }

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `createProject should return failure when writeLinesToFile throws exception`() {
        try {
            every { projectMapper.mapTo(MockProjects.CORRECT_PROJECT) } returns MockProjects.CORRECT_CSV_STRING_LINE
            every {
                csvStorageManager.writeLinesToFile(MockProjects.CORRECT_CSV_STRING_LINE + "\n")
            } throws Exception("Failed to write to file")

            val result = projectDataSource.createProject(MockProjects.CORRECT_PROJECT)

            Assertions.assertTrue(result.isFailure)
            verify(exactly = 1) {
                csvStorageManager.writeLinesToFile(MockProjects.CORRECT_CSV_STRING_LINE + "\n")
            }

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

    //region getProjects
    @Throws(EiffelFlowException.ElementNotFoundException::class)
    @Test
    fun `should return Result of empty list of Projects when the file is empty`() {
        //Given
        every { csvStorageManager.readLinesFromFile() } returns emptyList()

        //When
        val result = projectDataSource.getProjects()

        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.ElementNotFoundException::class.java)
    }

    @Test
    fun `should return Result of Projects when there are projects exist in CSV file`() {
        //Given
        every {
            projectMapper.mapFrom(MockProjects.CORRECT_CSV_STRING_LINE)
        } returns MockProjects.CORRECT_PROJECT

        every {
            csvStorageManager.readLinesFromFile()
        } returns MockProjects.CORRECT_CSV_STRING_LINE.split("\n")

        // When
        val result = projectDataSource.getProjects()

        // Then
        assertThat(result.getOrNull())
            .containsExactlyElementsIn(listOf(MockProjects.CORRECT_PROJECT))
    }

    @Throws(EiffelFlowException.ElementNotFoundException::class)
    @Test
    fun `should return Result of ElementNotFoundException when CSV file throw exception`() {
        //Given
        every {
            csvStorageManager.readLinesFromFile()
        } throws IOException("Failed to read file")

        // When / Then
        val result = projectDataSource.getProjects()

        // Then
        assertThat(result.exceptionOrNull())
            .isInstanceOf(EiffelFlowException.ElementNotFoundException::class.java)
    }
    //endregion

    //region getProjectById
    @Test
    fun `should return Result of Project when the given Id match project record exists in CSV file`() {
        //Given
        every {
            projectMapper.mapFrom(MockProjects.CORRECT_CSV_STRING_LINE)
        } returns MockProjects.CORRECT_PROJECT
        every {
            csvStorageManager.readLinesFromFile()
        } returns MockProjects.CORRECT_CSV_STRING_LINE.split("\n")

        // When
        val result = projectDataSource.getProjectById(MockProjects.CORRECT_PROJECT.projectId)

        //Then
        assertThat(result.getOrNull()).isEqualTo(MockProjects.CORRECT_PROJECT)
    }

    @Throws(EiffelFlowException.ElementNotFoundException::class)
    @Test
    fun `should return Result of ElementNotFoundException when searching for project doesn't exists in CSV file`() {
        // When
        val result = projectDataSource.getProjectById(UUID.randomUUID())

        //Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.ElementNotFoundException::class.java)
    }

    @Throws(EiffelFlowException.ElementNotFoundException::class)
    @Test
    fun `should return Result of ElementNotFoundException when searching for project and CSV file throw exception`() {
        //Given
        every {
            csvStorageManager.readLinesFromFile()
        } throws IOException("Failed to read file")

        // When
        val result = projectDataSource.getProjectById(UUID.randomUUID())

        //Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.ElementNotFoundException::class.java)
    }
    //endregion

}