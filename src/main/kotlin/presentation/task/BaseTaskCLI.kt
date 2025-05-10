package org.example.presentation.task

import kotlinx.coroutines.runBlocking
import org.example.data.utils.SessionManger
import org.example.domain.model.Project
import org.example.domain.model.Task
import org.example.domain.model.TaskState
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer

abstract class BaseTaskCLI(
    protected val inputReader: InputReader,
    protected val printer: Printer,
    private val getProjectUseCase: GetProjectUseCase
) : BaseCli(printer) {

    protected fun getProjects(): List<Project> = runBlocking { getProjectUseCase.getProjects() }

    protected fun getValidatedString(prompt: String): String {
        while (true) {
            printer.displayLn(prompt)
            val input = inputReader.readString()
            if (!input.isNullOrBlank()) return input
            printer.displayLn("Input cannot be empty.")
        }
    }

    protected fun getSelectedProject(items: List<Project>): Project {
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

    protected fun initTask(title: String, description: String, state: TaskState, project: Project): Task {
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
}
