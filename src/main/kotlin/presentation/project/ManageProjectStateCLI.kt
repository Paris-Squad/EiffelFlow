package org.example.presentation.project

import kotlinx.coroutines.runBlocking
import org.example.domain.model.Project
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer

class ManageProjectStateCLI(
    private val getProjectUseCase: GetProjectUseCase,
    private val createProjectStateCLI: CreateProjectStateCLI,
    private val deleteProjectStateCLI: DeleteProjectStateCLI,
    private val updateProjectStateCLI: UpdateProjectStateCLI,
    private val inputReader: InputReader,
    private val printer: Printer
) {
    fun start() {
        runBlocking {

            val selectedProject = selectProject()
            selectedProject ?: return@runBlocking

            printer.displayLn("--- Manage Project States ---")
            displayStates(selectedProject)

            println("-----------------Manage Project State CLI----------------------------")

            printer.displayLn("Please choose action:")

            printer.displayLn("1. Create new state")
            printer.displayLn("2. Update state")
            printer.displayLn("3. Delete state")

            val actionNumber = inputReader.readString()?.toIntOrNull() ?: 0
            if (actionNumber !in 1..3) {
                printer.displayLn("Invalid action number")
                return@runBlocking
            }

            when (actionNumber) {
                1 -> {
                    createProjectStateCLI.start()
                }

                2 -> {
                    printer.displayLn("Enter state number to update:")
                    updateProjectStateCLI.start()
                }

                3 -> {
                    printer.displayLn("Enter state number to delete: (Deleting a state will delete all tasks under it)")
                    deleteProjectStateCLI.start()
                }
            }
        }
    }

    private suspend fun selectProject(): Project? {
        printer.displayLn("\n--- Manage Project States ---")
        printer.displayLn("Please wait loading projects....")

        val allProjects = getProjectUseCase.getProjects()
        if (allProjects.isEmpty()) {
            printer.displayLn("No projects found. Add Project first")
            return null
        }

        allProjects.forEachIndexed { index, project ->
            printer.displayLn("${index + 1}. ${project.projectName}")
        }

        printer.displayLn("Enter project number to manage it's states: ")
        val input = inputReader.readString()?.toIntOrNull()
        if (input == null || input in 1..allProjects.size) {
            printer.displayLn("Number must be in range of projects 1-${allProjects.size}")
            return null
        }

        return allProjects[input]
    }

    private fun displayStates(project: Project) {
        project.taskStates.forEachIndexed { index, taskState ->
            printer.displayLn("${index + 1}. ${taskState.name}")
        }
    }
}