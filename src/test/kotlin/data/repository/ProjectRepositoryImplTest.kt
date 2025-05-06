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
import io.mockk.mockkObject
import io.mockk.runs
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.data.storage.FileDataSource
import org.example.data.storage.SessionManger
import org.example.data.storage.parser.ProjectCsvParser
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.ProjectRepository
import org.junit.jupiter.api.assertThrows
import utils.MockAuditLog
import utils.ProjectsMock
import utils.UserMock
import java.io.IOException

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
            projectCsvParser = projectMapper,
            fileDataSource = csvStorageManager,
            auditRepository = auditRepository
        )
    }

    //region createProject
    @Test
    fun `createProject should returns the project when projectRepository and auditRepository succeed`() {
        // Given
        every { sessionManger.getUser() } returns UserMock.adminUser
        every {
            projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)
        } returns ProjectsMock.CORRECT_PROJECT

        every { auditRepository.createAuditLog(any()) } returns
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

        //When
        val result = projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)

        //Then
        assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
        verify(exactly = 1) { auditRepository.createAuditLog(any()) }
    }

    @Test
    fun `createProject should throw IOException when projectRepository fails`() {
        //Given
        every { sessionManger.getUser() } returns UserMock.adminUser
        every {
            projectMapper.serialize(ProjectsMock.CORRECT_PROJECT)
        } throws Exception("Project creation failed")

        //When / Then
        assertThrows<EiffelFlowException.IOException> {
            projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)
        }

        verify(exactly = 0) { auditRepository.createAuditLog(any()) }
    }

    @Test
    fun `createProject should return AuthorizationException when user is not admin`() {
        // Given
        every { sessionManger.getUser() } returns (UserMock.validUser)

        // When / then
        assertThrows<EiffelFlowException.AuthorizationException> {
            projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)
        }
    }

    @Test
    fun `createProject should throw IOException when auditRepository fails`() {
        //Given
        every { sessionManger.getUser() } returns UserMock.adminUser
        every {
            projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)
        } returns ProjectsMock.CORRECT_PROJECT
        every {
            auditRepository.createAuditLog(any())
        } throws EiffelFlowException.IOException("Failed to create audit log")

        //When / Then
        assertThrows<EiffelFlowException.IOException> {
            projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)
        }
        verify(exactly = 1) { auditRepository.createAuditLog(any()) }

    }
    //endregion

    //region updateProject
    @Test
    fun `updateProject should throw AuthorizationException when user is not admin`() {
        // Given
        every { sessionManger.getUser() } returns (UserMock.validUser)

        // When / Then
        assertThrows<EiffelFlowException.AuthorizationException> {
            projectRepository.updateProject(
                project = ProjectsMock.updatedProject,
                oldProject = ProjectsMock.CORRECT_PROJECT,
                changedField = changedField
            )
        }
    }

    @Test
    fun `updateProject should return success if the project is updated`() {
        // Given
        every { sessionManger.isAdmin() } returns true
        every { sessionManger.getUser() } returns UserMock.adminUser
        every { projectMapper.serialize(ProjectsMock.CORRECT_PROJECT) } returns ProjectsMock.CORRECT_CSV_STRING_LINE
        every { projectMapper.serialize(ProjectsMock.updatedProject) } returns ProjectsMock.UPDATED_PROJECT_CSV
        every {
            csvStorageManager.updateLinesToFile(
                ProjectsMock.UPDATED_PROJECT_CSV,
                ProjectsMock.CORRECT_CSV_STRING_LINE
            )
        } just runs
        justRun { auditRepository.createAuditLog(MockAuditLog.AUDIT_LOG) }

        // When
        val result = projectRepository.updateProject(
            project = ProjectsMock.updatedProject,
            oldProject = ProjectsMock.CORRECT_PROJECT,
            changedField = "projectDescription"
        )

        // Then
        assertThat(result).isEqualTo(ProjectsMock.updatedProject)
    }



    @Test
    fun `updateProject should throw IOException when fileDataSource throws exception`() {
        // Given
        val exception = IOException("File write error")

        every { sessionManger.getUser() } returns UserMock.adminUser
        every {
            projectMapper.serialize(ProjectsMock.updatedProject)
        } returns ProjectsMock.UPDATED_PROJECT_CSV
        every {
            projectMapper.serialize(ProjectsMock.CORRECT_PROJECT)
        } returns ProjectsMock.CORRECT_CSV_STRING_LINE

        every {
            csvStorageManager.updateLinesToFile(
                ProjectsMock.UPDATED_PROJECT_CSV,
                ProjectsMock.CORRECT_CSV_STRING_LINE
            )
        } throws exception

        // When / Then
        assertThrows<EiffelFlowException.IOException> {
            projectRepository.updateProject(
                project = ProjectsMock.updatedProject,
                oldProject = ProjectsMock.CORRECT_PROJECT,
                changedField = changedField
            )
        }
    }

    @Test
    fun `updateProject should throw IOException when audit creation throws exception`() {

        every { sessionManger.getUser() } returns UserMock.adminUser
        every {
            projectMapper.serialize(ProjectsMock.updatedProject)
        } returns ProjectsMock.UPDATED_PROJECT_CSV
        every {
            projectMapper.serialize(ProjectsMock.CORRECT_PROJECT)
        } returns ProjectsMock.CORRECT_CSV_STRING_LINE
        every {
            csvStorageManager.updateLinesToFile(
                ProjectsMock.UPDATED_PROJECT_CSV,
                ProjectsMock.CORRECT_CSV_STRING_LINE
            )
        } just runs
        every {
            auditRepository.createAuditLog(any())
        } throws IOException("Audit log failed")

        // When / Then
        assertThrows<EiffelFlowException.IOException> {
            projectRepository.updateProject(
                project = ProjectsMock.updatedProject,
                oldProject = ProjectsMock.CORRECT_PROJECT,
                changedField = changedField
            )
        }
    }
    //endregion

    //region deleteProject
    @Test
    fun `deleteProject should return the deleted project`() {
        //  Given
        every { sessionManger.getUser() } returns UserMock.adminUser
        every {
            csvStorageManager.readLinesFromFile()
        } returns ProjectsMock.CORRECT_CSV_STRING_LINE.split("\n")

        every { csvStorageManager.writeLinesToFile(ProjectsMock.CORRECT_CSV_STRING_LINE) } returns Unit
        every {
            projectMapper.parseCsvLine(ProjectsMock.CORRECT_CSV_STRING_LINE)
        } returns ProjectsMock.CORRECT_PROJECT
        every {
            auditRepository.createAuditLog(MockAuditLog.AUDIT_LOG)
        } returns MockAuditLog.AUDIT_LOG

        // When
        val result = projectRepository.deleteProject(ProjectsMock.CORRECT_PROJECT.projectId)

        // Then
        assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
        verify { csvStorageManager.readLinesFromFile() }
        verify { csvStorageManager.writeLinesToFile(any()) }
        verify(exactly = 1) { auditRepository.createAuditLog(any()) }
    }

    @Test
    fun `deleteProject should throw IOException when project not found`() {
        // Given
        val differentProjectId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        every { sessionManger.getUser() } returns UserMock.adminUser
        every {
            csvStorageManager.readLinesFromFile()
        } returns ProjectsMock.CORRECT_CSV_STRING_LINE.split("\n")
        every {
            projectMapper.parseCsvLine(ProjectsMock.CORRECT_CSV_STRING_LINE)
        } returns ProjectsMock.CORRECT_PROJECT

        every {
            auditRepository.createAuditLog(any())
        } throws EiffelFlowException.IOException("Failed to create audit log")

        // When / Then
        assertThrows<EiffelFlowException.IOException> {
            projectRepository.deleteProject(differentProjectId)
        }
    }

    @Test
    fun `deleteProject should throw IOException when createAuditLog returns failure`() {
        // Given
        every { sessionManger.getUser() } returns UserMock.adminUser
        every { csvStorageManager.readLinesFromFile() } returns
                ProjectsMock.CORRECT_CSV_STRING_LINE.split("\n")
        every {
            projectMapper.parseCsvLine(ProjectsMock.CORRECT_CSV_STRING_LINE)
        } returns ProjectsMock.CORRECT_PROJECT
        every {
            auditRepository.createAuditLog(any())
        } throws EiffelFlowException.IOException("Failed to create audit log")

        // When /Then
        assertThrows<EiffelFlowException.IOException> {
            projectRepository.deleteProject(ProjectsMock.CORRECT_PROJECT.projectId)
        }
    }

    @Test
    fun `deleteProject should return AuthorizationException when the user is not the admin`() {
        // Given
        every { sessionManger.getUser() } returns UserMock.validUser

        // When /Then
        assertThrows<EiffelFlowException.AuthorizationException> {
            projectRepository.deleteProject(ProjectsMock.CORRECT_PROJECT.projectId)
        }
    }
    //endregion

    //region getProjects
    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should return empty list of Projects when the file is empty`() {
        //Given
        every { csvStorageManager.readLinesFromFile() } returns emptyList()

        //When
        val result = projectRepository.getProjects()

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `should return List of Projects when there are projects exist in CSV file`() {
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
    fun `should throw NotFoundException when CSV file throw exception`() {
        //Given
        every {
            csvStorageManager.readLinesFromFile()
        } throws IOException("Failed to read file")

        // When / Then
        assertThrows<EiffelFlowException.NotFoundException> {
            projectRepository.getProjects()
        }
    }
    //endregion

    //region getProjectById
    @Test
    fun `should return Project when the given Id match project record exists in CSV file`() {
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
        assertThat(result).isEqualTo(ProjectsMock.CORRECT_PROJECT)
    }

    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should throw NotFoundException when searching for project doesn't exists in CSV file`() {
        //When/ Them
        assertThrows<EiffelFlowException.NotFoundException> {
            projectRepository.getProjectById(UUID.randomUUID())
        }
    }

    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should return NotFoundException when searching for project and CSV file throw exception`() {
        //Given
        every {
            csvStorageManager.readLinesFromFile()
        } throws IOException("Failed to read file")

        //When/ Them
        assertThrows<EiffelFlowException.NotFoundException> {
            projectRepository.getProjectById(UUID.randomUUID())
        }
    }
    //endregion
}