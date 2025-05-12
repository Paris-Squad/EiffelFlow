package org.example.presentation.project

import kotlinx.coroutines.runBlocking
import org.example.domain.model.TaskState
import org.example.domain.usecase.project.CreateProjectStateUseCase
import org.example.domain.usecase.project.DeleteProjectStateUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import java.util.UUID

class DeleteProjectStateCLI(
    private val deleteProjectState: DeleteProjectStateUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {

    fun start() {
        tryStartCli {
            val projectId = readUUID(
                prompt = "Enter Project ID: ",
                errorMsg = "Project ID cannot be empty."
            ) ?: return@tryStartCli


            val stateId = readUUID(
                prompt = "Enter State ID to update: ",
                errorMsg = "Invalid or empty State ID."
            ) ?: return@tryStartCli

            runBlocking {
                val updatedProject = deleteProjectState.execute(projectId, stateId)
                printer.displayLn("Project state created successfully.")
                printer.displayLn("Project ID  : ${updatedProject.projectId}")
                printer.displayLn("State(s)    : ${updatedProject.taskStates}")
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
