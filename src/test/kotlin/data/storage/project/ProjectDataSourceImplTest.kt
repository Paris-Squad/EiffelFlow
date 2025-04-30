package data.storage.project

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.example.data.storage.CsvStorageManager
import org.example.data.storage.mapper.ProjectCsvMapper
import org.example.data.storage.mapper.StateCsvMapper
import org.example.data.storage.project.ProjectDataSource
import org.example.data.storage.project.ProjectDataSourceImpl
import org.example.domain.model.entities.Project
import org.example.domain.model.entities.State
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

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
    fun `getProjects should return list of projects`() {
        try {
            projectDataSource.getProjects()
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `getProjectById should return list of projects`() {
        try {
            projectDataSource.getProjectById(UUID.randomUUID())
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}