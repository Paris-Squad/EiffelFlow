package org.example.presentation.task

import kotlinx.coroutines.runBlocking
import org.example.domain.model.Project
import org.example.domain.model.Task
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.domain.usecase.task.EditTaskUseCase
import org.example.domain.usecase.task.GetTaskUseCase
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import java.util.*

class EditTaskCli(
    private val getTaskUseCase: GetTaskUseCase,
    private val editTaskUseCase: EditTaskUseCase,
    inputReader: InputReader,
    printer: Printer,
    getProjectUseCase: GetProjectUseCase
) : BaseTaskCLI(inputReader, printer, getProjectUseCase) {

    fun start() {
        tryStartCli {
            runBlocking {
                val selectedTask = selectTask()
                selectedTask ?: return@runBlocking

                val title = getValidatedString("Enter task title:")
                val description = getValidatedString("Enter task description:")
                val projects = getProjects()
                if (projects.isEmpty()) {
                    printer.displayLn("No projects found.")
                } else {
                    updateTask(projects, title, description, selectedTask)
                }
            }
        }
    }

    private suspend fun updateTask(
        projects: List<Project>,
        title: String,
        description: String,
        selectedTask: Task
    ) {

        printer.displayLn("Enter State: ")
        val project = projects.find { it.projectId == selectedTask.projectId } ?: return

        project.taskStates.forEachIndexed { index, state ->
            printer.displayLn("${index + 1}. ${state.name} (ID: ${state.stateId})")
        }

        val stateName = getValidatedString("Enter state name")

        val selectedState = project.taskStates.find { it.name == stateName } ?: return

        val newTask = Task(
            taskId = selectedTask.taskId,
            title = title,
            description = description,
            creatorId = selectedTask.creatorId,
            projectId = selectedTask.projectId,
            assignedId = selectedTask.assignedId,
            state = selectedState,
            role = selectedTask.role
        )


        editTaskUseCase.editTask(newTask)
        printer.displayLn("Task updated successfully: $newTask")
    }

    private suspend fun selectTask(): Task? {
        printer.displayLn("Please wait loading tasks....")

        val allTasks = getTaskUseCase.getTasks()
        if (allTasks.isEmpty()) {
            printer.displayLn("No tasks found. Add Task first")
            return null
        }

        allTasks.forEachIndexed { index, task ->
            printer.displayLn("${index + 1}. ${task.title} (ID: ${task.taskId})")
        }

        printer.displayLn("Enter Task ID to update: ")
        val input = inputReader.readString()
        if (input.isNullOrBlank()) {
            printer.displayLn("Tasks ID cannot be empty.")
            return null
        }
        val taskId = UUID.fromString(input)
        val selectedTask = allTasks.find { it.taskId == taskId }

        selectedTask ?: printer.displayLn("Please enter correct Id")

        return selectedTask
    }
}
