package org.example.presentation.presenter.project

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.usecase.project.CreateProjectUseCase
import org.example.presentation.presenter.io.InputReader
import org.example.presentation.presenter.io.Printer
import java.util.UUID
import kotlin.text.isNullOrBlank

class CreateProjectCLI(
    private val createProjectUseCase: CreateProjectUseCase ,
    private val inputReader: InputReader,
    private val printer: Printer
) {

    fun createProjectInput() {
        try {
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

            val createdProject = createProject(project)
            printer.displayLn("Project created successfully: $createdProject")

        } catch (e: EiffelFlowException) {
            printer.displayLn("Failed to create the project: ${e.message}")
        } catch (e: Exception) {
            printer.displayLn("An error occurred while creating the project: ${e.message}")
        }
    }

    fun createProject(project: Project): Project {
        return runBlocking {
            createProjectUseCase.createProject(project)
        }
    }
}