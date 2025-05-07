package org.example.presentation.presenter.project

import kotlinx.coroutines.runBlocking
import org.example.domain.model.Project
import org.example.domain.usecase.project.UpdateProjectUseCase

class UpdateProjectPresenter(
    private val updateProjectUseCase: UpdateProjectUseCase
) {
    fun updateProject(project: Project): Project {
        return runBlocking {
            updateProjectUseCase.updateProject(project)
        }
    }
}
