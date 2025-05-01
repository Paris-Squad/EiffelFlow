package org.example.data.storage.project

import org.example.domain.model.Project
import java.util.UUID

interface ProjectDataSource {

    fun createProject(project: Project): Result<Project>

    fun getProjects(): Result<List<Project>>

    fun getProjectById(projectId: UUID): Result<Project>

    fun deleteProject(projectID: UUID): Result<Project>

    fun updateProject(project: Project): Result<Project>
}