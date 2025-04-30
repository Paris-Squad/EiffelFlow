package org.example.presentation.presenter.project

import org.example.domain.model.entities.Project
import org.example.domain.usecase.project.GetProjectUseCase
import java.util.UUID

class GetProjectPresenter(
    private val getProjectUseCase: GetProjectUseCase
) {

    fun getProjectById(projectId: UUID): Result<Project> {
        TODO("Not yet implemented")
    }

    fun getProjects(): Result<List<Project>> {
        TODO("Not yet implemented")
    }
}
