package org.example.presentation.project

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import java.util.UUID

class GetProjectCLI(
    private val getProjectUseCase: GetProjectUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {

    fun start() {
        tryStartCli {
            val projects = getProjects()
            if (projects.isEmpty()) {
                printer.displayLn("No projects found.")
            } else {
                projects.forEachIndexed { index, project ->
                    printer.displayLn("${index + 1}. ${project.projectName} - ${project.projectDescription}")
                }
            }
        }

    }

    private fun getProjects(): List<Project> {
        return runBlocking {
            getProjectUseCase.getProjects()
        }
    }

    fun displayProjectById() {
        try {
            printer.displayLn("Enter project ID : ")
            val input = inputReader.readString()

            if (input.isNullOrBlank()) {
                printer.displayLn("Project ID cannot be empty.")
                return
            }

            val projectId = UUID.fromString(input.trim())
            val project = getProjectById(projectId)
            printer.displayLn("Project details : $project")

        } catch (_: IllegalArgumentException) {
            printer.displayLn("Invalid UUID format.")
        } catch (e: EiffelFlowException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("An error occurred while retrieving the project: ${e.message}", e)
        }
    }

    fun getProjectById(projectId: UUID): Project {
        return runBlocking {
            getProjectUseCase.getProjectById(projectId)
        }
    }

}
