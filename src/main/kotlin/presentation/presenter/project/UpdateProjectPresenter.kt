package org.example.presentation.presenter.project

import org.example.domain.model.Project
import org.example.domain.usecase.project.UpdateProjectUseCase

class UpdateProjectPresenter(
   private val updateProjectUseCase: UpdateProjectUseCase
) {
    fun updateProject(project: Project): Result<Project> {
        TODO("Not yet implemented")
    }
}
