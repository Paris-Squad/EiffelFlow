package org.example.presentation.presenter.project

import org.example.domain.model.Project

import org.example.domain.usecase.project.DeleteProjectUseCase
import java.util.UUID

class DeleteProjectPresenter(
    private val deleteProjectUseCase: DeleteProjectUseCase
) {

    fun deleteProject(projectId: UUID): Result<Project>{
        TODO("Not yet implemented")
    }
}