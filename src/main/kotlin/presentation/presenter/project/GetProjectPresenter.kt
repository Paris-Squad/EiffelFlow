package org.example.presentation.presenter.project

import org.example.domain.model.Project
import org.example.domain.usecase.project.GetProjectUseCase
import java.util.UUID

class GetProjectPresenter(
    private val getProjectUseCase: GetProjectUseCase
) {

    fun getProjectById(projectId: UUID): Project = getProjectUseCase.getProjectById(projectId)


    fun getProjects(): List<Project> = getProjectUseCase.getProjects()
}
