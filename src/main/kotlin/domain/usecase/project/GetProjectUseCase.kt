package org.example.domain.usecase.project

import org.example.domain.model.Project
import org.example.domain.repository.ProjectRepository
import java.util.UUID

class GetProjectUseCase(private val repository: ProjectRepository) {

    suspend fun getProjects(): List<Project> = repository.getProjects()

    suspend fun getProjectById(projectId: UUID): Project = repository.getProjectById(projectId)
}