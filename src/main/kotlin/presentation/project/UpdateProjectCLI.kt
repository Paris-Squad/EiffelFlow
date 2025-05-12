package org.example.presentation.project

import kotlinx.coroutines.runBlocking
import org.example.domain.model.Project
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.domain.usecase.project.UpdateProjectUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import java.util.UUID

class UpdateProjectCLI(
    private val getProjectUseCase: GetProjectUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {
    fun start() {
        return tryStartCli {
            runBlocking {
                val selectedProject = selectProject()
                selectedProject ?: return@runBlocking

                val project = ProjectInputHelper.collectProjectInput(inputReader, printer) ?: return@runBlocking

                val newProject = project.copy(
                    projectId = selectedProject.projectId,
                    createdAt = selectedProject.createdAt,
                    taskStates = selectedProject.taskStates
                )
                val updatedProject = updateProject(newProject)
                printer.displayLn("Project updated successfully: $updatedProject")
            }
        }
    }

    fun updateProject(project: Project): Project {
        return runBlocking {
            updateProjectUseCase.updateProject(project)
        }
    }

    private suspend fun selectProject(): Project? {
        printer.displayLn("Please wait loading projects....")

        val allProjects = getProjectUseCase.getProjects()
        if (allProjects.isEmpty()) {
            printer.displayLn("No projects found. Add Project first")
            return null
        }

        allProjects.forEachIndexed { index, project ->
            printer.displayLn("${index + 1}. ${project.projectName} (ID: ${project.projectId})")
        }

        printer.displayLn("Enter project ID to update: ")
        val input = inputReader.readString()
        if (input.isNullOrBlank()) {
            printer.displayLn("Project ID cannot be empty.")
            return null
        }
        val projectId = UUID.fromString(input)
        val selectedProject = allProjects.find { it.projectId == projectId }

        selectedProject ?: printer.displayLn("Please enter correct Id")

        return selectedProject
    }
}