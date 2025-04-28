package data.respoitory

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import org.example.data.respoitory.ProjectRepositoryImpl
import org.example.domain.model.Project
import org.example.domain.model.State
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

//todo change all of the test
class ProjectRepositoryImplTest {

    private lateinit var projectRepository: ProjectRepositoryImpl

    @BeforeEach
    fun setUp() {
        projectRepository = ProjectRepositoryImpl()
    }

    @Test
    fun `createProject should return the created project`() {
        val project = Project(
            title = "Test",
            description = "Test",
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
            title = "Test Project",
            description = "Updated Description",
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
}