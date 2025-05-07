package org.example.presentation.presenter.project

import kotlinx.coroutines.runBlocking
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.usecase.project.CreateProjectUseCase

class CreateProjectPresenter(
    private val createProjectUseCase: CreateProjectUseCase
) {

    fun createProject(project: Project): Project {
            return try {
                runBlocking {
                    createProjectUseCase.createProject(project)
                }
            } catch (e: EiffelFlowException) {
                throw e
            } catch (e: Exception) {
                throw RuntimeException("An error occurred while creating the project: ${e.message}", e)
            }
    }
}