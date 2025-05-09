package org.example.presentation.task

import kotlinx.coroutines.runBlocking
import org.example.data.storage.SessionManger
import org.example.domain.model.Project
import org.example.domain.model.Task
import org.example.domain.model.TaskState
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.domain.usecase.task.CreateTaskUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer

class CreateTaskCLI(
    private val createTaskUseCase: CreateTaskUseCase,
    private val getProjectUseCase: GetProjectUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {
    fun start() {
        tryStartCli {
            val title = getValidatedString("Enter task title:")
            val description = getValidatedString("Enter task description:")
            val projects = getProjects()
            if (projects.isEmpty()) {
                printer.displayLn("No projects found.")
            } else {
                createNewTask(projects, title, description)
            }
        }
    }

    private fun createNewTask(projects: List<Project>, title: String, description: String) {
        val stateName = getValidatedString("Enter state name")
        val project = getSelectedProject(projects)
        val initTask = initTask(title, description, TaskState(name = stateName), project)
        val task = createTask(initTask)
        printer.displayLn("Task created successfully: $task")
    }

    private fun getProjects(): List<Project> = runBlocking { getProjectUseCase.getProjects() }

    private fun createTask(task: Task) = runBlocking { createTaskUseCase.createTask(task) }

    private fun initTask(title: String, description: String, state: TaskState, project: Project): Task {
        val user = SessionManger.getUser()
        return Task(
            title = title,
            description = description,
            creatorId = user.userId,
            projectId = project.projectId,
            assignedId = user.userId,
            state = state,
            role = user.role
        )
    }

    private fun getValidatedString(prompt: String): String {
        while (true) {
            printer.displayLn(prompt)
            val input = inputReader.readString()
            if (!input.isNullOrBlank()) return input
            printer.displayLn("Input cannot be empty.")
        }
    }

    private fun getSelectedProject(items: List<Project>): Project {
        while (true) {
            items.forEachIndexed { index, item ->
                printer.displayLn("${index + 1} - $item")
            }
            printer.displayLn("Enter project number:")
            val input = inputReader.readString()?.toIntOrNull()
            val index = input?.minus(1)
            if (index != null && index in items.indices) {
                return items[index]
            } else {
                printer.displayLn("Please enter a valid number between 1 and ${items.size}")
            }
        }
    }

}