package org.example.presentation.project

import kotlinx.coroutines.runBlocking
import org.example.domain.usecase.project.UpdateProjectStateUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import java.util.UUID

class UpdateProjectStateCLI(
    private val updateProjectStateUseCase: UpdateProjectStateUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {

    fun start() {
        tryStartCli {
            val projectId = readUUID(
                prompt = "Enter Project ID: ",
                errorMsg = "Invalid or empty Project ID."
            ) ?: return@tryStartCli

            val stateId = readUUID(
                prompt = "Enter State ID to update: ",
                errorMsg = "Invalid or empty State ID."
            ) ?: return@tryStartCli

            val newStateName = readNonBlankInput(
                inputReader = inputReader,
                prompt = "Enter new state name:",
                errorMessage = "State name cannot be empty."
            ) ?: return@tryStartCli

            runBlocking {
                val updatedProject = updateProjectStateUseCase.execute(projectId, stateId, newStateName)
                printer.displayLn("Project state updated successfully.")
                printer.displayLn("Project ID  : ${updatedProject.projectId}")
                printer.displayLn("New State(s): ${updatedProject.taskStates}")
            }
        }
    }

    private fun readUUID(prompt: String, errorMsg: String): UUID? {
        val input = readNonBlankInput(inputReader, prompt, errorMsg)
        return try {
            UUID.fromString(input)
        } catch (e: Exception) {
            printer.displayLn("Invalid UUID format.")
            null
        }
    }
}
