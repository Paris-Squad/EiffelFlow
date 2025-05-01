package org.example.presentation.presenter.project

import org.example.domain.model.entities.Project
import org.example.domain.usecase.project.GetProjectUseCase
import java.util.UUID

class GetProjectPresenter(
    private val getProjectUseCase: GetProjectUseCase
) {

    fun getProjectById(projectId: UUID): Result<Project> {
        return getProjectUseCase.getProjectById(projectId)
    }

    fun getProjects(): Result<List<Project>> {
        return getProjectUseCase.getProjects()
    }
}
