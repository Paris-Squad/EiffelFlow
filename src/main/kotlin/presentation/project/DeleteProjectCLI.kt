package org.example.presentation.project


import kotlinx.coroutines.runBlocking
import org.example.domain.model.Project
import org.example.domain.usecase.project.DeleteProjectUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import java.util.UUID

class DeleteProjectCLI(
    private val deleteProjectUseCase: DeleteProjectUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {
    fun start() {
        tryStartCli {
            printer.displayLn("Deleting a Project will delete all tasks associated with it:")
            printer.displayLn("Please confirm by entering the Project ID or 'exit' to cancel:")
            val input = inputReader.readString()
            if (input == "exit") {
                return@tryStartCli
            }
            if (input.isNullOrBlank()) {
                printer.displayLn("Project ID cannot be empty.")
                return@tryStartCli
            }

            val projectId = UUID.fromString(input.trim())
            val deletedProject = deleteProject(projectId)
            printer.displayLn("Project deleted successfully $deletedProject")
        }
    }

    fun deleteProject(projectId: UUID): Project {
        return runBlocking {
            deleteProjectUseCase.deleteProject(projectId)
        }
    }

}
