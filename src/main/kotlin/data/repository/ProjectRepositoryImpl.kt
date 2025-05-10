package org.example.data.repository


import org.example.data.storage.FileDataSource
import org.example.data.storage.parser.ProjectCsvParser
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.repository.ProjectRepository
import java.util.UUID
import kotlin.jvm.Throws

class ProjectRepositoryImpl(
    private val projectCsvParser: ProjectCsvParser,
    private val fileDataSource: FileDataSource
) : ProjectRepository {

    @Throws(EiffelFlowException::class)
    override suspend fun createProject(project: Project): Project {

        return try {
            val csvLine = projectCsvParser.serialize(project)
            fileDataSource.writeLinesToFile(csvLine)
            project
        } catch (throwable: Throwable) {
            throw EiffelFlowException.IOException("Can't create project. ${throwable.message}")
        }
    }

    @Throws(EiffelFlowException::class)
    override suspend fun updateProject(project: Project, oldProject: Project, changedField: String): Project {

        return try {
            val projectCsv = projectCsvParser.serialize(project)
            val oldProjectCsv = projectCsvParser.serialize(oldProject)
            fileDataSource.updateLinesToFile(projectCsv, oldProjectCsv)
            project
        } catch (throwable: Throwable) {
            throw EiffelFlowException.IOException("Can't update project. ${throwable.message}")
        }
    }

    @Throws(EiffelFlowException::class)
    override suspend fun deleteProject(projectId: UUID): Project {

        return try {
            val projects = getProjects()

            val projectToDelete = projects.find { it.projectId == projectId }
                ?: throw EiffelFlowException.NotFoundException("Project not found")

            val projectCsv = projectCsvParser.serialize(projectToDelete)
            fileDataSource.deleteLineFromFile(projectCsv)
            projectToDelete
        } catch (throwable: Throwable) {
            throw EiffelFlowException.IOException(
                "Can't delete project. Project not found with ID: $projectId, ${throwable.message}"
            )
        }
    }

    @Throws(EiffelFlowException::class)
    override suspend fun getProjects(): List<Project> {
        return try {
            fileDataSource.readLinesFromFile()
                .map(projectCsvParser::parseCsvLine)
        } catch (throwable: Throwable) {
            throw EiffelFlowException.NotFoundException("No projects found, ${throwable.message}")
        }
    }

    @Throws(EiffelFlowException::class)
    override suspend fun getProjectById(projectId: UUID): Project {
        return try {
            fileDataSource.readLinesFromFile()
                .map(projectCsvParser::parseCsvLine)
                .firstOrNull { it.projectId == projectId }
                ?: throw EiffelFlowException.NotFoundException("Project not found")
        } catch (throwable: Throwable) {
            throw EiffelFlowException.NotFoundException("Project not found, ${throwable.message}")
        }
    }

    companion object {
        const val FILE_NAME: String = "projects.csv"
    }
}