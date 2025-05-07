package org.example.presentation.presenter.project

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.usecase.project.UpdateProjectUseCase

class UpdateProjectPresenter(
    private val updateProjectUseCase: UpdateProjectUseCase
) {
    fun updateProject(project: Project): Project {
        return try {
            runBlocking {
                updateProjectUseCase.updateProject(project)
            }
        } catch (e: EiffelFlowException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("An error occurred while updating the project: ${e.message}", e)
        }
    }
}
