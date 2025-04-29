package di

import org.example.data.storge.audit.AuditDataSource
import org.example.data.storge.mapper.AuditCsvMapper
import org.example.data.storge.mapper.ProjectCsvMapper
import org.example.data.storge.mapper.StateCsvMapper
import org.example.data.storge.mapper.TaskCsvMapper
import org.example.data.storge.mapper.UserCsvMapper
import org.example.data.storge.project.ProjectDataSource
import org.example.data.storge.task.TaskDataSource
import org.example.data.storge.user.UserDataSource
import org.example.di.appModule
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.ProjectRepository
import org.example.domain.repository.TaskRepository
import org.example.domain.repository.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get

class AppModuleTest : KoinTest {

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(appModule)
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `AuditCsvMapper can be resolved`() {
        val mapper = get<AuditCsvMapper>()
        assertNotNull(mapper)
    }

    @Test
    fun `ProjectCsvMapper can be resolved`() {
        val mapper = get<ProjectCsvMapper>()
        assertNotNull(mapper)
    }

    @Test
    fun `TaskCsvMapper can be resolved`() {
        val mapper = get<TaskCsvMapper>()
        assertNotNull(mapper)
    }

    @Test
    fun `StateCsvMapper can be resolved`() {
        val mapper = get<StateCsvMapper>()
        assertNotNull(mapper)
    }

    @Test
    fun `UserCsvMapper can be resolved`() {
        val mapper = get<UserCsvMapper>()
        assertNotNull(mapper)
    }

    @Test
    fun `AuditDataSource can be resolved`() {
        val dataSource = get<AuditDataSource>()
        assertNotNull(dataSource)
    }

    @Test
    fun `ProjectDataSource can be resolved`() {
        val dataSource = get<ProjectDataSource>()
        assertNotNull(dataSource)
    }

    @Test
    fun `TaskDataSource can be resolved`() {
        val dataSource = get<TaskDataSource>()
        assertNotNull(dataSource)
    }

    @Test
    fun `UserDataSource can be resolved`() {
        val dataSource = get<UserDataSource>()
        assertNotNull(dataSource)
    }

    @Test
    fun `AuditRepository can be resolved`() {
        val repository = get<AuditRepository>()
        assertNotNull(repository)
    }

    @Test
    fun `ProjectRepository can be resolved`() {
        val repository = get<ProjectRepository>()
        assertNotNull(repository)
    }

    @Test
    fun `TaskRepository can be resolved`() {
        val repository = get<TaskRepository>()
        assertNotNull(repository)
    }

    @Test
    fun `UserRepository can be resolved`() {
        val repository = get<UserRepository>()
        assertNotNull(repository)
    }
}