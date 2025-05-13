package org.example.presentation.project

import kotlinx.coroutines.runBlocking
import org.example.domain.usecase.project.DeleteProjectStateUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.Printer
import java.util.UUID

class DeleteProjectStateCLI(
    private val deleteProjectState: DeleteProjectStateUseCase,
    private val printer: Printer
) : BaseCli(printer) {

    fun start(projectId: UUID, stateId: UUID) {
        tryStartCli {
            printer.displayLn("Delete State Project")

            runBlocking {
                val updatedProject = deleteProjectState.execute(projectId, stateId)
                printer.displayLn("Project state deleted successfully.")
                printer.displayLn("Project ID  : ${updatedProject.projectId}")
                printer.displayLn("State(s)    : ${updatedProject.taskStates}")
            }
        }
    }
}

