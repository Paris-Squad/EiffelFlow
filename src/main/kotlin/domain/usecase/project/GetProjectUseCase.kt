package org.example.domain.usecase.project

import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.repository.ProjectRepository
import java.util.*

class GetProjectUseCase(private val repository: ProjectRepository) {

    suspend fun getProjects(): List<Project> {
        validateAdminPermission()
        return repository.getProjects()
    }

    suspend fun getProjectById(projectId: UUID): Project {
        validateAdminPermission()
        return repository.getProjectById(projectId)
    }

    private fun validateAdminPermission() {
        require(SessionManger.isAdmin()) {
            throw EiffelFlowException.AuthorizationException("Not Allowed, Admin only allowed to create project")
        }
    }
}