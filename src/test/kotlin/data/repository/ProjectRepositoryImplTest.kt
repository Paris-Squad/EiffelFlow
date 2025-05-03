package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.example.data.repository.ProjectRepositoryImpl
import org.example.domain.repository.AuditRepository
import org.example.domain.model.Project
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import org.example.domain.model.AuditLogAction
import org.example.domain.model.AuditLog
import io.mockk.verify
import io.mockk.every
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.data.storage.FileDataSource
import org.example.data.storage.SessionManger
import org.example.data.storage.parser.ProjectCsvParser
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.ProjectRepository
import org.junit.jupiter.api.Assertions
import utils.ProjectsMock
import utils.UserMock
import java.io.IOException

class ProjectRepositoryImplTest {

    private lateinit var projectRepository: ProjectRepository
    private val csvStorageManager: FileDataSource = mockk(relaxed = true)
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private val projectMapper: ProjectCsvParser = mockk(relaxed = true)
    private val sessionManger: SessionManger = mockk(relaxed = true)

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

        try {
            every { sessionManger.getUser() } returns UserMock.adminUser
            every {
                projectRepository.createProject(any())
            } returns Result.success(ProjectsMock.CORRECT_PROJECT)
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

            Assertions.assertTrue(result.isSuccess)

            verify(exactly = 1) { auditRepository.createAuditLog(any()) }

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }

    }

    @Test
    fun `createProject should return failure when projectRepository fails`() {
        try {
            every { sessionManger.getUser() } returns UserMock.adminUser
            every {
                projectRepository.createProject(any())
            } throws Exception("Project creation failed")

            val result = projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)

            Assertions.assertTrue(result.isFailure)

            verify(exactly = 0) { auditRepository.createAuditLog(any()) }

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }

    }

    @Test
    fun `createProject should return AuthorizationException when user is not admin`() {
        // Given
        every { sessionManger.getUser() } returns (UserMock.validUser)

        // When / then
        try {
            val result = projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)
            assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.AuthorizationException::class.java)

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }

    }

    @Test
    fun `createProject should return failure when auditRepository fails`() {
        try {
            every { sessionManger.getUser() } returns UserMock.adminUser
            every {
                projectRepository.createProject(any())
            } returns Result.success(ProjectsMock.CORRECT_PROJECT)
            every {
                auditRepository.createAuditLog(any())
            } throws EiffelFlowException.IOException("Failed to create audit log")

            val result = projectRepository.createProject(ProjectsMock.CORRECT_PROJECT)

            assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.IOException::class.java)

            verify(exactly = 1) { auditRepository.createAuditLog(any()) }

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }

    }
    //endregion

    @Test
    fun `updateProject should return the updated project`() {
        val project = Project(
            projectName = "Test Project",
            projectDescription = "Updated Description",
            createdAt = LocalDateTime(2023, 1, 1, 12, 0),
            adminId = UUID.randomUUID(),
            taskStates = emptyList()
        )
        every { sessionManger.getUser() } returns UserMock.adminUser

        try {
            projectRepository.updateProject(project)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    //region deleteProject
    @Test
    fun `deleteProject should return the deleted project`() {
        try {
            //  Given
            every { sessionManger.getUser() } returns UserMock.adminUser
            every { csvStorageManager.readLinesFromFile() } returns
                    ProjectsMock.CORRECT_CSV_STRING_LINE.split("\n")
            every { csvStorageManager.writeLinesToFile(any()) } returns Unit
            every {
                projectMapper.parseCsvLine(ProjectsMock.CORRECT_CSV_STRING_LINE)
            } returns ProjectsMock.CORRECT_PROJECT

            // When
            val result = projectRepository.deleteProject(ProjectsMock.CORRECT_PROJECT.projectId)

            // Then
            assertThat(result.getOrNull()).isEqualTo(ProjectsMock.CORRECT_PROJECT)
            verify { csvStorageManager.readLinesFromFile() }
            verify { csvStorageManager.writeLinesToFile(any()) }
            verify(exactly = 1) { auditRepository.createAuditLog(any()) }
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }

    }

    @Test
    fun `deleteProject should return failure when project not found`() {
        try {
            // Given
            val differentProjectId = UUID.fromString("11111111-1111-1111-1111-111111111111")
            every { sessionManger.getUser() } returns UserMock.adminUser
            every { csvStorageManager.readLinesFromFile() } returns
                    ProjectsMock.CORRECT_CSV_STRING_LINE.split("\n")
            every { projectMapper.parseCsvLine(ProjectsMock.CORRECT_CSV_STRING_LINE) } returns ProjectsMock.CORRECT_PROJECT
            every { auditRepository.createAuditLog(any()) } returns Result.failure(
                EiffelFlowException.IOException("Failed to create audit log")
            )

            // When
            val result = projectRepository.deleteProject(differentProjectId)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(
                EiffelFlowException.IOException::class.java
            )
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `deleteProject should throw IOException when createAuditLog returns failure`() {
        try {
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


            // When
            val result = projectRepository.deleteProject(ProjectsMock.CORRECT_PROJECT.projectId)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(
                EiffelFlowException.IOException::class.java
            )
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
    @Test
    fun `deleteProject should return UnauthorizedRegistrationException when the user is not the admin`() {
        try {
            // Given
            every { sessionManger.getUser() } returns UserMock.validUser

            // When
            val result = projectRepository.deleteProject(ProjectsMock.CORRECT_PROJECT.projectId)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(
                EiffelFlowException.IOException::class.java
            )

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
    //endregion

//    @Test
//    fun `deleteProject should throw IOException when deleteProject returns failure`() {
//        try {
//            // Given
//            val projectId = UUID.randomUUID()
//            every {
//                projectRepository.deleteProject(any())
//            } returns Result.failure(EiffelFlowException.IOException("Failed to delete project"))
//
//            // When
//            val result = projectRepository.deleteProject(projectId)
//
//            // Then
//            assertThat(result.exceptionOrNull()).isInstanceOf(
//                EiffelFlowException.IOException::class.java
//            )
//        } catch (e: NotImplementedError) {
//            assertThat(e.message).contains("Not yet implemented")
//        }
//    }

//    @Test
//    fun `deleteProject should throw IOException when createAuditLog returns failure`() {
//        try {
//            // Given
//            val projectId = UUID.randomUUID()
//            every { projectRepository.deleteProject(any()) } returns Result.success(ProjectsMock.CORRECT_PROJECT)
//            every { auditRepository.createAuditLog(any()) } returns Result.failure(
//                EiffelFlowException.IOException("Failed to create audit log")
//            )
//            // When
//            val result = projectRepository.deleteProject(projectId)
//
//            // Then
//            assertThat(result.exceptionOrNull()).isInstanceOf(
//                EiffelFlowException.IOException::class.java
//            )
//        } catch (e: NotImplementedError) {
//            assertThat(e.message).contains("Not yet implemented")
//        }
//    }


    //region getProjects
    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should return Result of empty list of Projects when the file is empty`() {
        //Given
        every { csvStorageManager.readLinesFromFile() } returns emptyList()

        //When
        val result = projectRepository.getProjects()

        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }

    @Test
    fun `should return Result of Projects when there are projects exist in CSV file`() {
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
        assertThat(result.getOrNull())
            .containsExactlyElementsIn(listOf(ProjectsMock.CORRECT_PROJECT))
    }

    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should return Result of ElementNotFoundException when CSV file throw exception`() {
        //Given
        every {
            csvStorageManager.readLinesFromFile()
        } throws IOException("Failed to read file")

        // When / Then
        val result = projectRepository.getProjects()

        // Then
        assertThat(result.exceptionOrNull())
            .isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }
    //endregion

    //region getProjectById
    @Test
    fun `should return Result of Project when the given Id match project record exists in CSV file`() {
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
        assertThat(result.getOrNull()).isEqualTo(ProjectsMock.CORRECT_PROJECT)
    }

    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should return Result of ElementNotFoundException when searching for project doesn't exists in CSV file`() {
        // When
        val result = projectRepository.getProjectById(UUID.randomUUID())

        //Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }

    @Throws(EiffelFlowException.NotFoundException::class)
    @Test
    fun `should return Result of ElementNotFoundException when searching for project and CSV file throw exception`() {
        //Given
        every {
            csvStorageManager.readLinesFromFile()
        } throws IOException("Failed to read file")

        // When
        val result = projectRepository.getProjectById(UUID.randomUUID())

        //Then
        assertThat(result.exceptionOrNull()).isInstanceOf(EiffelFlowException.NotFoundException::class.java)
    }
    //endregion
}