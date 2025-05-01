package org.example.domain.usecase.project

import org.example.domain.model.entities.Project
import org.example.domain.repository.ProjectRepository
import java.util.UUID

class GetProjectUseCase(private val repository: ProjectRepository) {

    fun getProjects(): Result<List<Project>> = repository.getProjects()

    fun getProjectById(projectId: UUID): Result<Project> = repository.getProjectById(projectId)
}