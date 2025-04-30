package org.example.domain.usecase.project

import org.example.domain.model.entities.Project
import org.example.domain.repository.ProjectRepository
import java.util.UUID

class GetProjectUseCase(private val repository: ProjectRepository) {

    fun getProjects(): Result<List<Project>> {
        TODO("Not yet implemented")
    }

    fun getProjectById(projectId: UUID): Result<Project> {
        TODO("Not yet implemented")
    }
}