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
        val state = getValidatedState("Enter state of task:")
        try {
            runBlocking {
                val listOfProject = getProjectUseCase.getProjects()
                printer.displayLn("Existing Projects:")
                showProjects(listOfProject)
                val project = getValidatedProject("Enter project number:")
                val initTask = initTask(title, description, state, project)
                val task=createTask(initTask)
                printer.displayLn("Task created successfully: $task")

            }

        } catch (e: EiffelFlowException) {
            printer.displayLn("Failed to create the task: ${e.message}")
        } catch (e: Exception) {
            printer.displayLn("An error occurred while creating the task: ${e.message}")
        }
    }

    private fun initTask(title: String, description: String, state: TaskState, project: Project): Task {
        return Task(
            title = title,
            description = description,
            creatorId = SessionManger.getUser().userId,
            projectId = project.projectId,
            assignedId = SessionManger.getUser().userId,
            state = state,
            role = SessionManger.getUser().role

        )
    }

    private fun getValidatedState(name: String): TaskState {
        val listOfState = listOf("todo","In Progress", "In Review", "Done")
        while (true) {
            listOfState.forEachIndexed { index, value ->
                val displayIndex = index + 1
                printer.displayLn("$displayIndex-$value")
            }
            printer.displayLn(name)
            val input = readlnOrNull()
            val validInput = getValidatedString(input)
            val actualIndex = validInput.toIntOrNull()?.minus(1)

            if (actualIndex !in listOfState.indices || actualIndex == null) {
                printer.displayLn("Please enter valid number between 1 and ${listOfState.size}")
            } else {
                return TaskState(name = listOfState[actualIndex])
            }

        }
    }

    private suspend fun getValidatedProject(title: String): Project {
        val listOfProject = getProjectUseCase.getProjects()
        while (true) {
            printer.displayLn(title)
            val input = inputReader.readString()?.toIntOrNull()
            val actualIndex = input?.minus(1)
            val sizeOfProject = getProjectUseCase.getProjects().size

            if (actualIndex == null||actualIndex !in 0..<sizeOfProject) {
                printer.displayLn("Please enter valid number of project")
            } else {
                return listOfProject[actualIndex]
            }
        }
    }

    private fun showProjects(listOfProject: List<Project>) {
        listOfProject.forEachIndexed { index, project ->
            val displayIndex = index + 1
            printer.displayLn("${displayIndex}-$project")
        }
    }

    private fun getValidatedString(name: String?): String {
        while (true) {
            printer.displayLn(name)
            val input = inputReader.readString()
            if (!input.isNullOrBlank()) return input
            printer.displayLn("Input cannot be empty.")
        }
    }


    fun createTask(task: Task): Task {
        return runBlocking {
            createTaskUseCase.createTask(task)
        }
    }
}