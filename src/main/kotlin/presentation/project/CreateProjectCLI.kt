package org.example.presentation.project

import kotlinx.coroutines.runBlocking
import org.example.domain.model.Project
import org.example.domain.usecase.project.CreateProjectUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer


class CreateProjectCLI(
    private val createProjectUseCase: CreateProjectUseCase,
    private val inputReader: InputReader,
    private val printer: Printer
) : BaseCli(printer) {

    fun start() {
        tryStartCli {
            val project = ProjectInputHelper.collectProjectInput(inputReader, printer) ?: return@tryStartCli
            val createdProject = createProject(project)
            printer.displayLn("Project created successfully: $createdProject")
        }
    }

    private fun createProject(project: Project): Project = runBlocking {
        createProjectUseCase.createProject(project)
    }
}