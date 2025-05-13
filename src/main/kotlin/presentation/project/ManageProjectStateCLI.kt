package org.example.presentation.project

import kotlinx.coroutines.runBlocking
import org.example.domain.model.Project
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer

class ManageProjectStateCLI(
    private val getProjectUseCase: GetProjectUseCase,
    private val createProjectStateCLI: CreateProjectStateCLI,
    private val updateProjectStateCLI: UpdateProjectStateCLI,
    private val deleteProjectStateCLI: DeleteProjectStateCLI,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {

    fun start() = runBlocking {
        printer.displayLn("✦•" + ("─".repeat(30)) + "| Manage Project Task States |" + ("─".repeat(30)) + "•✦")
        val selectedProject = selectProject() ?: return@runBlocking
        showTaskStates(selectedProject)
        handleAction(selectedProject)
    }

    private suspend fun selectProject(): Project? {
        val projects = getProjectUseCase.getProjects()
        if (projects.isEmpty()) {
            printer.displayLn("No projects found.")
            return null
        }

        printer.displayLn("Available Projects:")
        projects.forEachIndexed { index, project ->
            printer.displayLn(
                "${(index + 1).toString().padStart(2)}." + " Project Name ➜ ${project.projectName}".padEnd(25))  }

        val projectChoice = readNonBlankInput(
            inputReader,
            "Select a project by number: ",
            "Project selection cannot be empty."
        )?.toIntOrNull()

        return if (projectChoice != null && projectChoice in 1..projects.size)
            projects[projectChoice - 1]
        else {
            printer.displayLn("Invalid project selection.")
            null
        }
    }

    private fun showTaskStates(project: Project) {
        printer.displayLn("Project '${project.projectName}' States:")
        project.taskStates.forEachIndexed { index, taskState ->
            printer.displayLn("${index + 1}. ${taskState.name} (ID: ${taskState.stateId})")
        }
    }

    private fun handleAction(project: Project) {
        printer.displayLn("What do you want to do?")
        printer.displayLn("1. Create new state")
        printer.displayLn("2. Update existing state")
        printer.displayLn("3. Delete a state")

        when (inputReader.readString()?.toIntOrNull()) {
            1 -> createProjectStateCLI.start(project.projectId)
            2 -> handleUpdate(project)
            3 -> handleDelete(project)
            else -> printer.displayLn("Invalid action selected.")
        }
    }

    private fun handleUpdate(project: Project) {
        if (project.taskStates.isEmpty()) {
            printer.displayLn("No states to update.")
            return
        }

        val choice = readNonBlankInput(
            inputReader,
            "Select a state by number to update:",
            "State selection cannot be empty."
        )?.toIntOrNull()
        if (choice != null && choice in 1..project.taskStates.size) {
            val stateId = project.taskStates[choice - 1].stateId
            updateProjectStateCLI.start(project.projectId, stateId)
        } else {
            printer.displayLn("Invalid state selection.")
        }
    }

    private fun handleDelete(project: Project) {
        if (project.taskStates.isEmpty()) {
            printer.displayLn("No states to delete.")
            return
        }

        val choice = readNonBlankInput(
            inputReader,
            "Select a state by number to delete:",
            "State selection cannot be empty."
        )?.toIntOrNull()
        if (choice != null && choice in 1..project.taskStates.size) {
            val stateId = project.taskStates[choice - 1].stateId
            deleteProjectStateCLI.start(project.projectId, stateId)
        } else {
            printer.displayLn("Invalid state selection.")
        }
    }
}


