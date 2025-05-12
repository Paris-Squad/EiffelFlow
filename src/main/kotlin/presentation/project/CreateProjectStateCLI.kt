package org.example.presentation.project

import kotlinx.coroutines.runBlocking
import org.example.domain.model.TaskState
import org.example.domain.usecase.project.CreateProjectStateUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import java.util.UUID

class CreateProjectStateCLI(
    private val createProjectStateUseCase: CreateProjectStateUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {

    fun start() {
        tryStartCli {
            val projectIdStr = readNonBlankInput(
                inputReader,
                prompt = "Enter Project ID: ",
                errorMessage = "Project ID cannot be empty."
            ) ?: return@tryStartCli

            val projectId = try {
                UUID.fromString(projectIdStr)
            } catch (e: Exception) {
                printer.displayLn("Invalid UUID format.")
                return@tryStartCli
            }

            val newStateName = readNonBlankInput(
                inputReader,
                prompt = "Enter new state name:",
                errorMessage = "State name cannot be empty."
            ) ?: return@tryStartCli

            val newState = TaskState(name = newStateName)

            runBlocking {
                val updatedProject = createProjectStateUseCase.execute(projectId, newState)
                printer.displayLn("Project state created successfully.")
                printer.displayLn("Project ID  : ${updatedProject.projectId}")
                printer.displayLn("State(s)    : ${updatedProject.taskStates}")
            }
        }
    }
}
