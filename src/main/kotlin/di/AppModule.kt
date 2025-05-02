package org.example.di

import org.example.data.repository.*
import org.example.data.storage.FileDataSource
import org.example.data.storage.mapper.*
import org.example.data.storage.task.TaskDataSource
import org.example.data.storage.task.TaskDataSourceImpl
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
    single<TaskDataSource> {
        TaskDataSourceImpl(
            taskMapper = get<TaskCsvMapper>(),
            csvManager = FileDataSource(File(TaskDataSourceImpl.FILE_NAME))
        )
    }

    //repose
    single<AuditRepository> {
        AuditRepositoryImpl(
            auditMapper = get<AuditCsvMapper>(),
            csvManager = FileDataSource(File(AuditRepositoryImpl.FILE_NAME))
        )
    }
    single<ProjectRepository> {
        ProjectRepositoryImpl(
            projectMapper = get<ProjectCsvMapper>(),
            csvManager = FileDataSource(File(ProjectRepositoryImpl.FILE_NAME)),
            auditRepository = get()
        )
    }
    single<TaskRepository> { TaskRepositoryImpl(get(), get()) }
    single<UserRepository> { 
        UserRepositoryImpl(
            userMapper = get<UserCsvMapper>(),
            csvManager = FileDataSource(File(UserRepositoryImpl.FILE_NAME)),
            auditRepository = get()
        ) 
    }
    single<AuthRepository> {
        AuthRepositoryImpl(FileDataSource(File(AuthRepositoryImpl.FILE_NAME)) , get())
    }
}