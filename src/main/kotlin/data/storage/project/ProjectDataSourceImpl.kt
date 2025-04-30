package org.example.data.storage.project

import org.example.data.storage.CsvStorageManager
import org.example.data.storage.Mapper
import org.example.domain.model.entities.Project
import org.example.domain.model.exception.EiffelFlowException
import java.util.UUID

class ProjectDataSourceImpl(
    private val projectMapper: Mapper<String, Project>,
    private val csvManager: CsvStorageManager
) : ProjectDataSource {

    override fun getProjects(): Result<List<Project>> = runCatching {
        csvManager.readLinesFromFile()
            .map(projectMapper::mapFrom)
            .ifEmpty {
                throw EiffelFlowException.ElementNotFoundException("No projects found")
            }
    }

    override fun getProjectById(projectId: UUID): Result<Project> = runCatching {
        csvManager.readLinesFromFile()
            .map(projectMapper::mapFrom)
            .firstOrNull { it.projectId == projectId }
            ?: throw EiffelFlowException.ElementNotFoundException("Project not found")
    }

    override fun createProject(project: Project): Result<Project> {
        TODO("Not yet implemented")
    }

    override fun deleteProject(projectID: UUID): Result<Project> {
        TODO("Not yet implemented")
    }

    override fun updateProject(project: Project): Result<Project> {
        TODO("Not yet implemented")
    }

    companion object {
        const val FILE_NAME: String = "projects.csv"
    }
}