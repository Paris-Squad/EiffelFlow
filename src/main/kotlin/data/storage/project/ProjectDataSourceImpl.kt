package org.example.data.storage.project

import org.example.data.storage.CsvStorageManager
import org.example.data.storage.Mapper
import org.example.domain.model.Project
import org.example.domain.exception.EiffelFlowException
import java.util.UUID

class ProjectDataSourceImpl(
    private val projectMapper: Mapper<String, Project>,
    private val csvManager: CsvStorageManager
) : ProjectDataSource {

    override fun getProjects(): Result<List<Project>> {
        return runCatching {
            csvManager.readLinesFromFile()
                .map(projectMapper::mapFrom)
                .ifEmpty {
                    throw EiffelFlowException.NotFoundException("No projects found")
                }
        }.recoverCatching {
            throw EiffelFlowException.NotFoundException("No projects found")
        }
    }

    override fun getProjectById(projectId: UUID): Result<Project> {
        return runCatching {
            csvManager.readLinesFromFile()
                .map(projectMapper::mapFrom)
                .firstOrNull { it.projectId == projectId }
                ?: throw EiffelFlowException.NotFoundException("Project not found")
        }.recoverCatching {
            throw EiffelFlowException.NotFoundException("Project not found")
        }
    }

    override fun createProject(project: Project): Result<Project> {
        return try {
            val csvLine = projectMapper.mapTo(project)
            csvManager.writeLinesToFile(csvLine + "\n")
            Result.success(project)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun deleteProject(projectID: UUID): Result<Project> {
        val lines = csvManager.readLinesFromFile().toMutableList()

        val removedLine = lines.find { line->
            val project = projectMapper.mapFrom(line)
            project.projectId == projectID
        } ?: return Result.failure(EiffelFlowException.IOException("Can't delete project. Project not found with ID: $projectID."))

        lines.remove(removedLine)
        csvManager.writeLinesToFile(lines.joinToString("\n"))
        return Result.success(projectMapper.mapFrom(removedLine))
    }

    override fun updateProject(project: Project): Result<Project> {
        TODO("Not yet implemented")
    }

    companion object {
        const val FILE_NAME: String = "projects.csv"
    }
}