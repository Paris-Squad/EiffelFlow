package org.example.domain.usecase.project

import org.example.domain.model.entities.Project
import org.example.domain.repository.ProjectRepository

class UpdateProjectUseCase (private val repository: ProjectRepository) {
    fun updateProject(project : Project) : Result<Project>{
        return repository.updateProject(project)
    }
}