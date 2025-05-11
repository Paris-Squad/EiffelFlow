package org.example.di
import data.remote.repository.AuditRepositoryImpl
import data.remote.repository.AuthRepositoryImpl
import data.remote.repository.ProjectRepositoryImpl
import data.remote.repository.TaskRepositoryImpl
import data.remote.repository.UserRepositoryImpl
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.AuthRepository
import org.example.domain.repository.ProjectRepository
import org.example.domain.repository.TaskRepository
import org.example.domain.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuditRepository> {
        AuditRepositoryImpl(
            database = get(),
            taskRepositoryProvider = lazy { get() },
            auditLogMapper = get()
        )
    }
    single<ProjectRepository> {
        ProjectRepositoryImpl(database = get(), projectMapper = get())
    }
    single<TaskRepository> {
        TaskRepositoryImpl(database = get(), taskMapper = get())
    }
    single<UserRepository> {
        UserRepositoryImpl(database = get(), userMapper = get())
    }
    single<AuthRepository> {
        AuthRepositoryImpl(database = get(), userMapper = get())
    }

    /*single<AuditRepository> {
        CsvAuditRepositoryImpl(
            auditCsvParser = get<AuditCsvParser>(),
            fileDataSource = FileDataSource(File(CsvAuditRepositoryImpl.FILE_NAME)),
            taskRepositoryProvider = lazy { get() }
        )
    }
    single<ProjectRepository> {
        CsvProjectRepositoryImpl(
            projectCsvParser = get<ProjectCsvParser>(),
            fileDataSource = FileDataSource(File(CsvProjectRepositoryImpl.FILE_NAME)),
        )
    }
    single<TaskRepository> {
        CsvTaskRepositoryImpl(
            taskCsvParser = get<TaskCsvParser>(),
            fileDataSource = FileDataSource(File(CsvTaskRepositoryImpl.FILE_NAME))
        )
    }
    single<UserRepository> {
        CsvUserRepositoryImpl(
            userCsvParser = get<UserCsvParser>(),
            fileDataSource = FileDataSource(File(CsvUserRepositoryImpl.FILE_NAME))
        )
    }
    single<AuthRepository> {
        CsvAuthRepositoryImpl(
            authFileDataSource = FileDataSource(File(CsvAuthRepositoryImpl.FILE_NAME)),
            usersFileDataSource = FileDataSource(File(CsvUserRepositoryImpl.FILE_NAME)),
            userCsvParser = get()
        )
    }*/
}
