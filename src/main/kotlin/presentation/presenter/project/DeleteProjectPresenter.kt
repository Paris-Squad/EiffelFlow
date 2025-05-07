package org.example.presentation.presenter.project


import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.usecase.project.DeleteProjectUseCase
import java.util.UUID

class DeleteProjectPresenter(
    private val deleteProjectUseCase: DeleteProjectUseCase
) {
    fun deleteProject(projectId : UUID): Project {
        return try {
            runBlocking {
                deleteProjectUseCase.deleteProject(projectId)
            }
        } catch (e: EiffelFlowException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("An error occurred while deleting the project: ${e.message}", e)
        }
    }
}