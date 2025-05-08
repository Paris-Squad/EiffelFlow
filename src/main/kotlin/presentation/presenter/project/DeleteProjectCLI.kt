package org.example.presentation.presenter.project


import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.usecase.project.DeleteProjectUseCase
import org.example.presentation.presenter.io.InputReader
import org.example.presentation.presenter.io.Printer
import java.util.UUID

class DeleteProjectCLI(
    private val deleteProjectUseCase: DeleteProjectUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) {
    fun deleteProjectInput() {
        try {
            printer.displayLn("Enter project ID to delete: ")
            val input = inputReader.readString()

            if (input.isNullOrBlank()) {
                printer.displayLn("Project ID cannot be empty.")
                return
            }

            val projectId = UUID.fromString(input.trim())
            val deletedProject = deleteProject(projectId)
            printer.displayLn("Project deleted successfully $deletedProject")
        } catch (e: IllegalArgumentException) {
            printer.displayLn("Invalid UUID format.")
        } catch (e: EiffelFlowException) {
            printer.displayLn("Failed to delete the project: ${e.message}")
        } catch (e: Exception) {
            printer.displayLn("An error occurred while deleting the project: ${e.message} ")
        }
    }

     fun deleteProject(projectId: UUID): Project {
       return runBlocking {
            deleteProjectUseCase.deleteProject(projectId)
        }
    }

}
