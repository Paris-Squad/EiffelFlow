package org.example.presentation.task

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Task
import org.example.domain.usecase.task.GetTaskUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import java.util.UUID

class GetTaskCLI(
    private val getTaskUseCase: GetTaskUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
): BaseCli(printer) {

    fun start(){
        tryStartCli {
            val tasks = getTasks()
            if(tasks.isEmpty()){
                printer.displayLn("No Tasks found.")
            }else{
                tasks.forEachIndexed { index, task ->
                    printer.displayLn("${index + 1}.${task.title} - ${task.description}")
                }
            }
        }
    }

    private fun getTasks(): List<Task>{
        return runBlocking {
            getTaskUseCase.getTasks()
        }
    }

    fun displayTaskById() {
        try {
            printer.displayLn("Enter Task ID : ")
            val input = inputReader.readString()

            if (input.isNullOrBlank()) {
                printer.displayLn("Task ID cannot be empty.")
                return
            }

            val taskId = UUID.fromString(input.trim())
            val task = getProjectById(taskId)
            printer.displayLn("Task details : $task")

        } catch (_: IllegalArgumentException) {
            printer.displayLn("Invalid UUID format.")
        } catch (e: EiffelFlowException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("An error occurred while retrieving the Task: ${e.message}", e)
        }
    }
    private fun getProjectById(taskId: UUID): Task{
        return runBlocking {
            getTaskUseCase.getTaskByID(taskId)
        }
    }
}