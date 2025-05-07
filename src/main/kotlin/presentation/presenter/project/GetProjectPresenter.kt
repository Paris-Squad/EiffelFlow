package org.example.presentation.presenter.project

import kotlinx.coroutines.runBlocking
import org.example.domain.model.Project
import org.example.domain.usecase.project.GetProjectUseCase
import java.util.UUID

class GetProjectPresenter(
    private val getProjectUseCase: GetProjectUseCase
) {

    fun getProjectById(projectId: UUID): Project{
        return runBlocking {
            getProjectUseCase.getProjectById(projectId)
        }
    }

    fun getProjects(): List<Project> {
        return runBlocking {
            getProjectUseCase.getProjects()
        }
    }
}
