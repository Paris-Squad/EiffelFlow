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

    fun start(projectId: UUID, stateId: UUID) {
        tryStartCli {
            printer.displayLn("Update Existing Task State")

            val newStateName = readNonBlankInput(
                inputReader = inputReader,
                prompt = "Enter new state name:",
                errorMessage = "State name cannot be empty or blank."
            ) ?: return@tryStartCli

            runBlocking {
                val updatedProject = updateProjectStateUseCase.execute(projectId, stateId, newStateName)
                printer.displayLn("Project state updated successfully.")
                printer.displayLn("Project ID  : ${updatedProject.projectId}")
                printer.displayLn("New State(s): ${updatedProject.taskStates}")
            }
        }
    }
}
