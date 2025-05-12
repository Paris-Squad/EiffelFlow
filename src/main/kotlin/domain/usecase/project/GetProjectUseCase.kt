package org.example.domain.usecase.project

import org.example.domain.model.Project
import org.example.domain.repository.ProjectRepository
import java.util.*

class GetProjectUseCase(private val repository: ProjectRepository) {

    suspend fun getProjects(): List<Project> {
        return repository.getProjects()
    }

    suspend fun getProjectById(projectId: UUID): Project {
        return repository.getProjectById(projectId)
    }

}