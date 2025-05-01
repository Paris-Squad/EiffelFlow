package org.example.domain.repository

import org.example.domain.model.entities.Project
import java.util.UUID

interface ProjectRepository {

    fun createProject(project: Project): Result<Project>

    fun updateProject(project: Project): Result<Project>

    fun deleteProject(projectId: UUID): Result<Project>

    fun getProjectById(projectId: UUID): Result<Project>

    fun getProjects(): Result<List<Project>>
}