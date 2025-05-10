package org.example.presentation.project

import kotlinx.coroutines.runBlocking
import org.example.domain.model.Project
import org.example.domain.usecase.project.UpdateProjectUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer

class UpdateProjectCLI(
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer){
    fun start(){
        return tryStartCli {
            val project = ProjectInputHelper.collectProjectInput(inputReader, printer) ?: return@tryStartCli
            val updatedProject = updateProject(project)
            printer.displayLn("Project updated successfully: $updatedProject")
        }
    }

    fun updateProject(project: Project): Project {
        return runBlocking {
            updateProjectUseCase.updateProject(project)
        }
    }
}