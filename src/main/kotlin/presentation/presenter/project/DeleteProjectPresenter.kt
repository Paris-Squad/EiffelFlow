package org.example.presentation.presenter.project


import kotlinx.coroutines.runBlocking
import org.example.domain.model.Project
import org.example.domain.usecase.project.DeleteProjectUseCase
import java.util.UUID

class DeleteProjectPresenter(
    private val deleteProjectUseCase: DeleteProjectUseCase
) {
    fun deleteProject(projectId : UUID): Project {
        return runBlocking {
            deleteProjectUseCase.deleteProject(projectId)
        }
    }
}