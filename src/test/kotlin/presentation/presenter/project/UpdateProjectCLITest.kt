package presentation.presenter.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.example.data.utils.SessionManger
import org.example.domain.model.Project
import org.example.domain.usecase.project.UpdateProjectUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.project.UpdateProjectCLI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ProjectsMock
import utils.UserMock
import java.util.*

class UpdateProjectCLITest {

    private val updateProjectUseCase: UpdateProjectUseCase = mockk()
    private lateinit var updateProjectCli: UpdateProjectCLI
    private val inputReader: InputReader = mockk()
    private val printer: Printer = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        mockkObject(SessionManger)
        every { SessionManger.getUser() } returns UserMock.adminUser
        updateProjectCli =
            UpdateProjectCLI(
                updateProjectUseCase = updateProjectUseCase,
                inputReader = inputReader,
                printer = printer
            )
    }

    @Test
    fun `should return the updated Project when project is successfully updated`() {
        coEvery {
            updateProjectUseCase.updateProject(ProjectsMock.CORRECT_PROJECT)
        } returns ProjectsMock.CORRECT_PROJECT

        val result = updateProjectCli.updateProject(ProjectsMock.CORRECT_PROJECT)

        assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
    }

    @Test
    fun `should update project successfully with valid input`() {
        val adminId = UUID.randomUUID()
        val expectedProject = Project(
            projectName = "Project1",
            projectDescription = "Desc",
            adminId = SessionManger.getUser().userId
        )

        every { inputReader.readString() } returnsMany listOf("Project1", "Desc")
        coEvery { updateProjectUseCase.updateProject(any()) } returns expectedProject

        updateProjectCli.start()

        verify { printer.displayLn("Project updated successfully: $expectedProject") }
    }

    @Test
    fun `should return updated project when multiple fields of project are updated`() {
        // Given
        val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(
            projectName = "Updated Project Name",
            projectDescription = "Updated Description"
        )
        coEvery { updateProjectUseCase.updateProject(updatedProject) } returns updatedProject

        // When
        val result = updateProjectCli.updateProject(updatedProject)

        // Then
        assertThat(result).isEqualTo(updatedProject)
    }

    @Test
    fun `should print error when project name is empty`() {
        every { inputReader.readString() } returns ""

        updateProjectCli.start()

        verify { printer.displayLn("Project name cannot be empty.") }
    }

    @Test
    fun `should print error when project name is null`() {
        every { inputReader.readString() } returns null

        updateProjectCli.start()

        verify { printer.displayLn("Project name cannot be empty.") }
    }

    @Test
    fun `should print error when description is empty`() {
        every { inputReader.readString() } returnsMany listOf("Project1", "")

        updateProjectCli.start()

        verify { printer.displayLn("Project description cannot be empty.") }
    }

    @Test
    fun `should print error when description is null`() {
        every { inputReader.readString() } returnsMany listOf("Project1", null)

        updateProjectCli.start()

        verify { printer.displayLn("Project description cannot be empty.") }
    }
}
