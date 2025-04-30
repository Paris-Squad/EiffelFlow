package org.example.domain.usecase.project

import org.example.domain.model.entities.Project
import org.example.domain.repository.ProjectRepository

class CreateProjectUseCase(private val repository: ProjectRepository) {
    fun createProject(project : Project) : Result<Project>{
        return repository.createProject(project)
    }
}