package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.example.data.repository.ProjectRepositoryImpl
import org.example.domain.repository.AuditRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import org.example.domain.model.AuditLogAction
import org.example.domain.model.AuditLog
import io.mockk.verify
import io.mockk.every
import io.mockk.just
import io.mockk.justRun
import io.mockk.runs
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.data.storage.FileDataSource
import org.example.data.storage.SessionManger
import org.example.data.storage.parser.ProjectCsvParser
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.repository.ProjectRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows
import utils.ProjectsMock
import utils.ProjectsMock.CORRECT_PROJECT
import utils.UserMock
import java.io.IOException
import kotlin.test.assertEquals

class ProjectRepositoryImplTest {

    private lateinit var projectRepository: ProjectRepository
    private val csvStorageManager: FileDataSource = mockk(relaxed = true)
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private val projectMapper: ProjectCsvParser = mockk(relaxed = true)
    private val sessionManger: SessionManger = mockk(relaxed = true)
    private val changedField = "projectDescription"

    @BeforeEach
    fun setUp() {
        projectRepository = ProjectRepositoryImpl(
            projectCsvParser = projectMapper, fileDataSource = csvStorageManager, auditRepository = auditRepository
        )
    }

    //region createProject
    @Test
    fun `createProject should returns the project when projectRepository and auditRepository succeed`() {
        every { sessionManger.getUser() } returns UserMock.adminUser
        every {
            projectRepository.createProject(CORRECT_PROJECT)
        } returns ProjectsMock.CORRECT_PROJECT
        every { auditRepository.createAuditLog(any()) } returns Result.success(
            AuditLog(
                auditId = UUID.randomUUID(),
                itemId = ProjectsMock.CORRECT_PROJECT.projectId,
                itemName = ProjectsMock.CORRECT_PROJECT.projectName,
                userId = ProjectsMock.CORRECT_PROJECT.adminId,
                editorName = "Admin",
                actionType = AuditLogAction.CREATE,
                auditTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                changedField = null,
                oldValue = null,
                newValue = ProjectsMock.CORRECT_PROJECT.projectName
            )
        )

        val result = projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)

        assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)

        verify(exactly = 1) { auditRepository.createAuditLog(any()) }

    }

    @Test
    fun `createProject should return failure when projectRepository fails`() {
        every { sessionManger.getUser() } returns UserMock.adminUser
        every {
            projectMapper.serialize(CORRECT_PROJECT)
        } throws Exception("Project creation failed")

        assertThrows<EiffelFlowException.IOException> {
            projectRepository.createProject(CORRECT_PROJECT)
        }
    }

    @Test
    fun `createProject should return AuthorizationException when user is not admin`() {
        // Given
        every { sessionManger.getUser() } returns (UserMock.validUser)

        assertThrows<EiffelFlowException.AuthorizationException> {
            projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)
        }

    }

    @Test
    fun `createProject should return failure when auditRepository fails`() {
        every { sessionManger.getUser() } returns UserMock.adminUser
        every {
            projectRepository.createProject(CORRECT_PROJECT)
        } returns CORRECT_PROJECT
        every {
            auditRepository.createAuditLog(any())
        } throws EiffelFlowException.IOException("Failed to create audit log")

        assertThrows<EiffelFlowException.IOException> {
            projectRepository.createProject(CORRECT_PROJECT)
        }
        verify(exactly = 1) { auditRepository.createAuditLog(any()) }

    }
    //endregion


    //region updateProject
    @Test
    fun `updateProject should return failure when user is not admin`() {
        // Given
        every { sessionManger.getUser() } returns (UserMock.validUser)

        // Then
        assertThrows<EiffelFlowException.AuthorizationException> {
            projectRepository.updateProject(
                project = updatedProject,
                oldProject = ProjectsMock.CORRECT_PROJECT,
                changedField = changedField
            )
        }
    }

    @Test
    fun `updateProject should return success if the project is updated`() {

        every { sessionManger.getUser() } returns UserMock.adminUser
        every { projectMapper.serialize(ProjectsMock.CORRECT_PROJECT) } returns oldProjectCsv
        every { projectMapper.serialize(updatedProject) } returns updatedProjectCsv
        every { csvStorageManager.updateLinesToFile(updatedProjectCsv, oldProjectCsv) } just runs
        justRun { auditRepository.createAuditLog(any()) }


        // When
        val result = projectRepository.updateProject(
            project = updatedProject, oldProject = ProjectsMock.CORRECT_PROJECT, changedField = "projectDescription"
        )

        // Then
        assertThat(result).isEqualTo(updatedProject)
    }

    @Test
    fun `updateProject should return failure when fileDataSource throws exception`() {
        // Given
        val exception = IOException("File write error")

        every { sessionManger.getUser() } returns UserMock.adminUser
        every { projectMapper.serialize(updatedProject) } returns updatedProjectCsv
        every { projectMapper.serialize(ProjectsMock.CORRECT_PROJECT) } returns oldProjectCsv

        every { csvStorageManager.updateLinesToFile(updatedProjectCsv, oldProjectCsv) } throws exception

        assertThrows<EiffelFlowException.IOException> {
            projectRepository.updateProject(
                project = updatedProject, oldProject = ProjectsMock.CORRECT_PROJECT, changedField = changedField
            )
        }
    }

    @Test
    fun `updateProject should return failure when audit creation throws exception`() {

        every { sessionManger.getUser() } returns UserMock.adminUser
        every { projectMapper.serialize(updatedProject) } returns updatedProjectCsv
        every { projectMapper.serialize(ProjectsMock.CORRECT_PROJECT) } returns oldProjectCsv
        every { csvStorageManager.updateLinesToFile(updatedProjectCsv, oldProjectCsv) } just runs
        every { auditRepository.createAuditLog(any()) } throws IOException("Audit log failed")

        assertThrows<EiffelFlowException.IOException> {
            projectRepository.updateProject(
                project = updatedProject, oldProject = ProjectsMock.CORRECT_PROJECT, changedField = changedField
            )
        }
    }
    //endregion


    //region deleteProject
    @Test
    fun `deleteProject should return the deleted project`() {
        //  Given
        every { sessionManger.getUser() } returns UserMock.adminUser
        every { csvStorageManager.readLinesFromFile() } returns ProjectsMock.CORRECT_CSV_STRING_LINE.split("\n")
        every { csvStorageManager.writeLinesToFile(any()) } returns Unit
        every {
            projectMapper.parseCsvLine(ProjectsMock.CORRECT_CSV_STRING_LINE)
        } returns ProjectsMock.CORRECT_PROJECT

        // When
        val result = projectRepository.deleteProject(ProjectsMock.CORRECT_PROJECT.projectId)

        // Then
        assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
        verify { csvStorageManager.readLinesFromFile() }
        verify { csvStorageManager.writeLinesToFile(any()) }
        verify(exactly = 1) { auditRepository.createAuditLog(any()) }
    }

    @Test
    fun `deleteProject should return failure when project not found`() {
        // Given
        val differentProjectId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        every { sessionManger.getUser() } returns UserMock.adminUser
        every { csvStorageManager.readLinesFromFile() } returns ProjectsMock.CORRECT_CSV_STRING_LINE.split("\n")
        every { projectMapper.parseCsvLine(ProjectsMock.CORRECT_CSV_STRING_LINE) } returns ProjectsMock.CORRECT_PROJECT
        every { auditRepository.createAuditLog(any()) } returns Result.failure(
            EiffelFlowException.IOException("Failed to create audit log")
        )

        assertThrows<EiffelFlowException.IOException> {
            projectRepository.deleteProject(differentProjectId)
        }
    }

    @Test
    fun `deleteProject should throw IOException when createAuditLog returns failure`() {
        every { sessionManger.getUser() } returns UserMock.adminUser
        every { csvStorageManager.readLinesFromFile() } returns ProjectsMock.CORRECT_CSV_STRING_LINE.split("\n")
        every {
            projectMapper.parseCsvLine(ProjectsMock.CORRECT_CSV_STRING_LINE)
        } returns ProjectsMock.CORRECT_PROJECT
        every {
            auditRepository.createAuditLog(any())
        } throws EiffelFlowException.IOException("Failed to create audit log")

        assertThrows<EiffelFlowException.IOException> {
            projectRepository.deleteProject(CORRECT_PROJECT.projectId)
        }
    }

    @Test
    fun `deleteProject should return AuthorizationException when the user is not the admin`() {
        every { sessionManger.getUser() } returns UserMock.validUser

        assertThrows<EiffelFlowException.AuthorizationException> {
            projectRepository.deleteProject(CORRECT_PROJECT.projectId)
        }
    }
    //endregion
    
    //region getProjects
    @Test
    fun `should return empty list of Projects when the file is empty`() {
        //Given
        every { csvStorageManager.readLinesFromFile() } returns emptyList()

        //When
        val result = projectRepository.getProjects()
        
        assertThat(result).isEqualTo(emptyList<Project>())
    }

    @Test
    fun `should return Projects when there are projects exist in CSV file`() {
        //Given
        every {
            projectMapper.parseCsvLine(ProjectsMock.CORRECT_CSV_STRING_LINE)
        } returns ProjectsMock.CORRECT_PROJECT

        every {
            csvStorageManager.readLinesFromFile()
        } returns ProjectsMock.CORRECT_CSV_STRING_LINE.split("\n")

        // When
        val result = projectRepository.getProjects()

        // Then
        assertThat(result).containsExactlyElementsIn(listOf(ProjectsMock.CORRECT_PROJECT))
    }

    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should return ElementNotFoundException when CSV file throw exception`() {
        //Given
        every { sessionManger.getUser() } returns UserMock.adminUser
        every {
            csvStorageManager.readLinesFromFile()
        } throws IOException("Failed to read file")

        // Then
        assertThrows<EiffelFlowException.IOException> {
            projectRepository.deleteProject(CORRECT_PROJECT.projectId)
        }
    }
    //endregion

    //region getProjectById
    @Test
    fun `should return  Project when the given Id match project record exists in CSV file`() {
        //Given
        every {
            projectMapper.parseCsvLine(ProjectsMock.CORRECT_CSV_STRING_LINE)
        } returns ProjectsMock.CORRECT_PROJECT
        every {
            csvStorageManager.readLinesFromFile()
        } returns ProjectsMock.CORRECT_CSV_STRING_LINE.split("\n")

        // When
        val result = projectRepository.getProjectById(ProjectsMock.CORRECT_PROJECT.projectId)

        //Then
        assertThat(result).isEqualTo(CORRECT_PROJECT)
    }

    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should return ElementNotFoundException when searching for project doesn't exists in CSV file`() {
        assertThrows<EiffelFlowException.NotFoundException> {
            projectRepository.getProjectById(UUID.randomUUID())
        }
    }

    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should return ElementNotFoundException when searching for project and CSV file throw exception`() {
        //Given
        every { sessionManger.getUser() } returns UserMock.adminUser
        every {
            csvStorageManager.readLinesFromFile()
        } throws IOException("Failed to read file")

        assertThrows<EiffelFlowException.IOException> {
            projectRepository.getProjectById(UUID.randomUUID())
        }
    }
    //endregion

    companion object {
        val updatedProject = ProjectsMock.CORRECT_PROJECT.copy(projectDescription = "UpdatedProject")
        const val oldProjectCsv = ProjectsMock.CORRECT_CSV_STRING_LINE
        const val updatedProjectCsv = "id1,Project1,UpdatedProject,1999-08-07T22:22:22,admin-id,..."
    }

}