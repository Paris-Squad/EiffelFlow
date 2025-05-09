package org.example.presentation.task


import kotlinx.coroutines.runBlocking
import org.example.domain.model.Task
import org.example.domain.usecase.task.DeleteTaskUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import java.util.UUID

class DeleteTaskCLI(
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {
    fun deleteTaskInput() {
        tryStartCli {
            printer.displayLn("Enter Task ID to delete: ")
            val input = inputReader.readString()

            if (input.isNullOrBlank()) {
                printer.displayLn("Task ID cannot be empty.")
                return@tryStartCli
            }

            val taskId = UUID.fromString(input.trim())
              deleteTask(taskId)
            printer.displayLn("Task deleted successfully")
        }
    }

    fun deleteTask(taskId: UUID): Task {
        return runBlocking {
            deleteTaskUseCase.deleteTask(taskId)
        }
    }

}
