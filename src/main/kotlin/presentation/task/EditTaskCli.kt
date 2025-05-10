package org.example.presentation.task

import kotlinx.coroutines.runBlocking
import org.example.domain.model.Project
import org.example.domain.model.TaskState
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.domain.usecase.task.EditTaskUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer

class EditTaskCli(
    private val editTaskUseCase: EditTaskUseCase,
    inputReader: InputReader,
    printer: Printer,
    getProjectUseCase: GetProjectUseCase
) : BaseTaskCLI(inputReader, printer, getProjectUseCase) {

    fun start() {
        tryStartCli {
            val title = getValidatedString("Enter task title:")
            val description = getValidatedString("Enter task description:")
            val projects = getProjects()
            if (projects.isEmpty()) {
                printer.displayLn("No projects found.")
            } else {
                updateTask(projects, title, description)
            }
        }
    }

    private fun updateTask(projects: List<Project>, title: String, description: String) {
        val stateName = getValidatedString("Enter state name")
        val project = getSelectedProject(projects)
        val task = initTask(title, description, TaskState(name = stateName), project)
        runBlocking { editTaskUseCase.editTask(task) }
        printer.displayLn("Task updated successfully: $task")
    }
}
