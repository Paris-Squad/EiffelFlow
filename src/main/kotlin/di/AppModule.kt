package org.example.di

import org.example.data.repository.*
import org.example.data.storage.CsvStorageManager
import org.example.data.storage.mapper.*
import org.example.data.storage.project.ProjectDataSource
import org.example.data.storage.project.ProjectDataSourceImpl
import org.example.data.storage.task.TaskDataSource
import org.example.data.storage.task.TaskDataSourceImpl
import org.example.data.storage.user.UserDataSource
import org.example.data.storage.user.UserDataSourceImpl
import org.example.domain.repository.*
import org.koin.dsl.module
import java.io.File

val appModule = module {

    single<AuditCsvMapper> { AuditCsvMapper() }
    single<ProjectCsvMapper> { ProjectCsvMapper(get()) }
    single<TaskCsvMapper> { TaskCsvMapper(get()) }
    single<StateCsvMapper> { StateCsvMapper() }
    single<UserCsvMapper> { UserCsvMapper() }

    //DataSource
    single<ProjectDataSource> {
        ProjectDataSourceImpl(
            projectMapper = get<ProjectCsvMapper>(),
            csvManager = CsvStorageManager(File(ProjectDataSourceImpl.FILE_NAME))
        )
    }
    single<TaskDataSource> {
        TaskDataSourceImpl(
            taskMapper = get<TaskCsvMapper>(),
            csvManager = CsvStorageManager(File(TaskDataSourceImpl.FILE_NAME))
        )
    }

    single<UserDataSource> {
        UserDataSourceImpl(
            userMapper = get<UserCsvMapper>(),
            csvManager = CsvStorageManager(File(UserDataSourceImpl.FILE_NAME))
        )
    }

    //repose
    single<AuditRepository> { AuditRepositoryImpl(
        auditMapper = get<AuditCsvMapper>(),
        csvManager = CsvStorageManager(File(AuditRepositoryImpl.FILE_NAME))
    ) }
    single<ProjectRepository> { ProjectRepositoryImpl(get(), get()) }
    single<TaskRepository> { TaskRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<AuthRepository> {
        AuthRepositoryImpl(CsvStorageManager(File(AuthRepositoryImpl.FILE_NAME)))
    }
}