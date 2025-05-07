package org.example.domain.usecase.project

import org.example.domain.model.Project
import org.example.domain.repository.ProjectRepository
import java.util.UUID

class DeleteProjectUseCase(private val projectRepository: ProjectRepository) {

    suspend fun deleteProject(projectId: UUID):Project = projectRepository.deleteProject(projectId)

}