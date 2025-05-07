package org.example.domain.repository

import org.example.domain.model.Project
import java.util.UUID

interface ProjectRepository {

    suspend fun createProject(project: Project): Project

    suspend fun updateProject(project: Project ,oldProject: Project ,changedField: String): Project

    suspend fun deleteProject(projectId: UUID): Project

    suspend fun getProjectById(projectId: UUID): Project

    suspend fun getProjects(): List<Project>
}