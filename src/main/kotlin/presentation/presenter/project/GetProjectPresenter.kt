package org.example.presentation.presenter.project

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.usecase.project.GetProjectUseCase
import java.util.UUID

class GetProjectPresenter(
    private val getProjectUseCase: GetProjectUseCase
) {

    fun getProjectById(projectId: UUID): Project{
        return try {
            runBlocking {
                getProjectUseCase.getProjectById(projectId)
            }
        } catch (e: EiffelFlowException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("An error occurred while retrieving the project: ${e.message}", e)
        }
    }

    fun getProjects(): List<Project> {
        return try {
            runBlocking {
                getProjectUseCase.getProjects()
            }
        } catch (e: EiffelFlowException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("An error occurred while retrieving the projects: ${e.message}", e)
        }
    }
}
