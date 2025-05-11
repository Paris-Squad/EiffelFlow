package org.example.presentation.task

import kotlinx.coroutines.runBlocking
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

    fun viewTasks(){
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
        tryStartCli {
            printer.displayLn("Enter Task ID : ")
            val input = inputReader.readString()

            if (input.isNullOrBlank()) {
                printer.displayLn("Task ID cannot be empty.")
                return@tryStartCli
            }

            val taskId = UUID.fromString(input.trim())
            val task = getTaskById(taskId)
            printer.displayLn("Task details : $task")
        }
    }
    private fun getTaskById(taskId: UUID): Task{
        return runBlocking {
            getTaskUseCase.getTaskByID(taskId)
        }
    }
}