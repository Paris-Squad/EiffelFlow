package org.example.data.storge.project

import org.example.data.storge.CsvStorageManager
import org.example.data.storge.Mapper
import org.example.data.storge.mapper.StateCsvMapper
import org.example.domain.model.entities.Project
import java.util.UUID

class ProjectDataSourceImpl(
    private val projectMapper: Mapper<List<String>, Project>,
    private val stateCsvMapper: StateCsvMapper,
    private val csvManager: CsvStorageManager
) : ProjectDataSource {

    override fun getProjects(): Result<List<Project>> {
        TODO("Not yet implemented")
    }

    override fun getProjectById(projectID: UUID): Result<Project> {
        TODO("Not yet implemented")
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