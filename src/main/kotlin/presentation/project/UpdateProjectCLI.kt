package org.example.presentation.project

import kotlinx.coroutines.runBlocking
import org.example.domain.model.Project
import org.example.domain.usecase.project.UpdateProjectUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import java.util.UUID

class UpdateProjectCLI(
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer){
    fun updateProjectInput(){
        return tryStartCli {
            printer.displayLn("Enter project name:")
            val name = inputReader.readString()
            if (name.isNullOrBlank()) {
                printer.displayLn("Project name cannot be empty.")
                return@tryStartCli
            }

            printer.displayLn("Enter project description:")
            val description = inputReader.readString()
            if (description.isNullOrBlank()) {
                printer.displayLn("Project description cannot be empty.")
                return@tryStartCli
            }

            printer.displayLn("Enter admin ID:")
            val adminIdInput = inputReader.readString()
            val adminId = try {
                UUID.fromString(adminIdInput)
            } catch (e: IllegalArgumentException) {
                printer.displayLn("Invalid admin ID format.")
                return@tryStartCli
            }

            val project = Project(
                projectName = name,
                projectDescription = description,
                adminId = adminId
            )

            val createdProject = updateProject(project)
            printer.displayLn("Project updated successfully: $createdProject")

        }
    }




    fun updateProject(project: Project): Project {
        return runBlocking {
                updateProjectUseCase.updateProject(project)
            }
    }
}
