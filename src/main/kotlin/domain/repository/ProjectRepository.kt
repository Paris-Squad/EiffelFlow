package org.example.domain.repository

import org.example.domain.model.Project
import java.util.UUID

interface ProjectRepository {

    fun createProject(project: Project): Project

    fun updateProject(project: Project ,oldProject: Project ,changedField: String): Project

    fun deleteProject(projectId: UUID): Project

    fun getProjectById(projectId: UUID): Project

    fun getProjects(): List<Project>
}