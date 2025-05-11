package org.example.data.local.csvrepository

import org.example.data.BaseRepository
import org.example.data.local.FileDataSource
import org.example.data.local.parser.ProjectCsvParser
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.repository.ProjectRepository
import java.util.UUID

class ProjectRepositoryImpl(
    private val projectCsvParser: ProjectCsvParser,
    private val fileDataSource: FileDataSource
) : BaseRepository(), ProjectRepository {

    @Throws(EiffelFlowException::class)
    override suspend fun createProject(project: Project): Project {
        return wrapInTryCatch {
            val csvLine = projectCsvParser.serialize(project)
            fileDataSource.writeLinesToFile(csvLine)
            project
        }
    }

    @Throws(EiffelFlowException::class)
    override suspend fun updateProject(project: Project, oldProject: Project, changedField: String): Project {
        return wrapInTryCatch {
            val projectCsv = projectCsvParser.serialize(project)
            val oldProjectCsv = projectCsvParser.serialize(oldProject)
            fileDataSource.updateLinesToFile(projectCsv, oldProjectCsv)
            project
        }
    }

    @Throws(EiffelFlowException::class)
    override suspend fun deleteProject(projectId: UUID): Project {
        return wrapInTryCatch {
            val projects = getProjects()

            val projectToDelete = projects.find { it.projectId == projectId }
                ?: throw EiffelFlowException.NotFoundException("Project not found")

            val projectCsv = projectCsvParser.serialize(projectToDelete)
            fileDataSource.deleteLineFromFile(projectCsv)
            projectToDelete
        }
    }

    @Throws(EiffelFlowException::class)
    override suspend fun getProjects(): List<Project> {
        return wrapInTryCatch {
            fileDataSource.readLinesFromFile()
                .map(projectCsvParser::parseCsvLine)
        }
    }

    @Throws(EiffelFlowException::class)
    override suspend fun getProjectById(projectId: UUID): Project {
        return wrapInTryCatch {
            fileDataSource.readLinesFromFile()
                .map(projectCsvParser::parseCsvLine)
                .firstOrNull { it.projectId == projectId }
                ?: throw EiffelFlowException.NotFoundException("Project not found")
        }
    }

    companion object {
        const val FILE_NAME: String = "projects.csv"
    }
}