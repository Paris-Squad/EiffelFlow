package org.example.presentation.presenter.project

import org.example.domain.model.entities.Project
import org.example.domain.usecase.project.CreateProjectUseCase

class CreateProjectPresenter(
    private val createProjectUseCase: CreateProjectUseCase
) {
    fun createProject(project: Project): Result<Project> {
        TODO("Not yet implemented")
    }
}