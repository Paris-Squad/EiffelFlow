package org.example.presentation.presenter.project

import kotlinx.coroutines.runBlocking
import org.example.domain.model.Project
import org.example.domain.usecase.project.CreateProjectUseCase

class CreateProjectPresenter(
    private val createProjectUseCase: CreateProjectUseCase
) {

    fun createProject(project: Project): Project {
        return runBlocking {
            createProjectUseCase.createProject(project)
        }
    }

}