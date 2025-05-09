package org.example.presentation.presenter.task

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Task
import org.example.domain.usecase.task.DeleteTaskUseCase
import org.example.presentation.presenter.io.InputReader
import org.example.presentation.presenter.io.Printer
import java.util.*


class DeleteTaskCLI(
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
)  {
    fun deleteTaskInput() {
        try {
            printer.displayLn("Enter Task ID to delete: ")
            val input = inputReader.readString()

            if (input.isNullOrBlank()) {
                printer.displayLn("Task ID cannot be empty.")
                return
            }

            val taskId = UUID.fromString(input.trim())
             deleteTask(taskId)
            printer.displayLn("Task deleted successfully")
        }catch (e: IllegalArgumentException) {
            printer.displayLn("Invalid UUID format.")
        } catch (e: EiffelFlowException) {
            printer.displayLn("Failed to delete the task: ${e.message}")
        } catch (e: Exception) {
            printer.displayLn("An error occurred while deleting the task: ${e.message} ")
        }
    }

    fun deleteTask(taskId: UUID): Task {
        return runBlocking {
            deleteTaskUseCase.deleteTask(taskId)
        }
    }

}
