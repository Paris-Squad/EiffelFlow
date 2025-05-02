package org.example.domain.usecase.project

import org.example.domain.model.Project
import org.example.domain.repository.ProjectRepository

class CreateProjectUseCase(private val repository: ProjectRepository) {
    fun createProject(project : Project) : Result<Project> = repository.createProject(project)
}
