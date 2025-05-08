package presentation.presenter.project

import org.example.domain.usecase.project.UpdateProjectUseCase
import org.example.presentation.presenter.project.UpdateProjectCLI
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.presentation.presenter.io.InputReader
import org.example.presentation.presenter.io.Printer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ProjectsMock
import java.util.UUID

class UpdateProjectCLITest {

    private val updateProjectUseCase: UpdateProjectUseCase = mockk()
    private lateinit var updateProjectCli: UpdateProjectCLI
    private val inputReader: InputReader = mockk()
    private val printer: Printer = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        updateProjectCli = UpdateProjectCLI(updateProjectUseCase = updateProjectUseCase, inputReader = inputReader, printer = printer)
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
            adminId =  adminId
        )

        every { inputReader.readString() } returnsMany listOf("Project1", "Desc", adminId.toString())
        coEvery { updateProjectUseCase.updateProject(any()) } returns expectedProject

        updateProjectCli.updateProjectInput()

        verify { printer.displayLn("Project updated successfully: $expectedProject") }
    }

    @Test
    fun `should throw Exception when unknown exception occurs during input flow`() {
        val adminId = UUID.randomUUID()

        every { inputReader.readString() } returnsMany listOf("Project1", "Description", adminId.toString())

        coEvery {
            updateProjectUseCase.updateProject(
                match {
                    it.projectName == "Project1" &&
                            it.projectDescription == "Description" &&
                            it.adminId == adminId
                }
            )
        } throws IllegalStateException("Unexpected failure")

        val exception = assertThrows<RuntimeException> {
            updateProjectCli.updateProjectInput()
        }

        assertThat(exception.message).isEqualTo("An error occurred while updating the project: Unexpected failure")
    }

    @Test
    fun `should rethrow EiffelFlowException when updateProjectUseCase throws Exception`() {
        val adminId = UUID.randomUUID()

        every { inputReader.readString() } returnsMany listOf("Project1", "Description", adminId.toString())

        coEvery {
            updateProjectUseCase.updateProject(
                match { it.projectName == "Project1" && it.projectDescription == "Description" && it.adminId == adminId }
            ) } throws EiffelFlowException.IOException("Simulated IO error")

        assertThrows<EiffelFlowException.IOException> { updateProjectCli.updateProjectInput() }
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

        updateProjectCli.updateProjectInput()

        verify { printer.displayLn("Project name cannot be empty.") }
    }

    @Test
    fun `should print error when project name is null`() {
        every { inputReader.readString() } returns null

        updateProjectCli.updateProjectInput()

        verify { printer.displayLn("Project name cannot be empty.") }
    }

    @Test
    fun `should print error when description is empty`() {
        every { inputReader.readString() } returnsMany listOf("Project1", "")

        updateProjectCli.updateProjectInput()

        verify { printer.displayLn("Project description cannot be empty.") }
    }

    @Test
    fun `should print error when description is null`() {
        every { inputReader.readString() } returnsMany listOf("Project1", null)

        updateProjectCli.updateProjectInput()

        verify { printer.displayLn("Project description cannot be empty.") }
    }

    @Test
    fun `should print error when admin ID format is invalid`() {
        every { inputReader.readString() } returnsMany listOf("Project1", "Description", "not-a-uuid")

        updateProjectCli.updateProjectInput()

        verify { printer.displayLn("Invalid admin ID format.") }
    }


}
