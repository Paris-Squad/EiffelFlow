package org.example.presentation.presenter.project

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.usecase.project.UpdateProjectUseCase
import org.example.presentation.presenter.io.InputReader
import org.example.presentation.presenter.io.Printer
import java.util.UUID

class UpdateProjectCLI(
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) {
    fun updateProjectInput(){
        return try {
            printer.displayLn("Enter project name:")
            val name = inputReader.readString()
            if (name.isNullOrBlank()) {
                printer.displayLn("Project name cannot be empty.")
                return
            }

            printer.displayLn("Enter project description:")
            val description = inputReader.readString()
            if (description.isNullOrBlank()) {
                printer.displayLn("Project description cannot be empty.")
                return
            }

            printer.displayLn("Enter admin ID:")
            val adminIdInput = inputReader.readString()
            val adminId = try {
                UUID.fromString(adminIdInput)
            } catch (e: IllegalArgumentException) {
                printer.displayLn("Invalid admin ID format.")
                return
            }

            val project = Project(
                projectName = name,
                projectDescription = description,
                adminId = adminId
            )

            val createdProject = updateProject(project)
            printer.displayLn("Project updated successfully: $createdProject")

        } catch (e: EiffelFlowException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("An error occurred while updating the project: ${e.message}", e)
        }
    }




    fun updateProject(project: Project): Project {
        return runBlocking {
                updateProjectUseCase.updateProject(project)
            }
    }
}
