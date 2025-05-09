package org.example.presentation.presenter.task

import kotlinx.coroutines.runBlocking
import org.example.data.storage.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.model.Task
import org.example.domain.model.TaskState
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.domain.usecase.task.CreateTaskUseCase
import org.example.presentation.presenter.io.InputReader
import org.example.presentation.presenter.io.Printer

class CreateTaskCLI(
    private val createTaskUseCase: CreateTaskUseCase,
    private val getProjectUseCase: GetProjectUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) {
    fun createTaskInput() {
        val title = getValidatedString("Enter task title:")
        val description = getValidatedString("Enter task description:")

     return   try {
            runBlocking {
                val projects = getProjectUseCase.getProjects()
                if(projects.isEmpty()) return@runBlocking

                val states = listOf("todo", "In Progress", "In Review", "Done")
                    .map { TaskState(name = it) }

                val project = getValidatedChoice(projects, "Enter project number:")
                val state = getValidatedChoice(states, "Enter state of task:")

                val initTask = initTask(title, description, state, project)
                val task = createTask(initTask)
                printer.displayLn("Task created successfully: $task")
            }
        } catch (e: EiffelFlowException) {
            printer.displayLn("Failed to create the task: ${e.message}")
        } catch (e: Exception) {
            printer.displayLn("An error occurred while creating the task: ${e.message}")
        }
    }

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

    private fun getValidatedString(prompt: String?): String {
        while (true) {
            printer.displayLn(prompt)
            val input = inputReader.readString()
            if (!input.isNullOrBlank()) return input
            printer.displayLn("Input cannot be empty.")
        }
    }

    private fun <T> getValidatedChoice(items: List<T>, prompt: String): T {
        while (true) {
            items.forEachIndexed { index, item ->
                printer.displayLn("${index + 1} - $item")
            }
            printer.displayLn(prompt)
            val input = inputReader.readString()?.toIntOrNull()
            val index = input?.minus(1)
            if (index != null && index in items.indices) {
                return items[index]
            } else {
                printer.displayLn("Please enter a valid number between 1 and ${items.size}")
            }
        }
    }

     private suspend fun createTask(task: Task): Task = createTaskUseCase.createTask(task)
}
