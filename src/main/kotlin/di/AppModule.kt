package org.example.di

import org.example.data.respoitory.AuditRepositoryImpl
import org.example.data.respoitory.ProjectRepositoryImpl
import org.example.data.respoitory.TaskRepositoryImpl
import org.example.data.respoitory.UserRepositoryImpl
import org.example.data.storge.CsvStorageManager
import org.example.data.storge.audit.AuditDataSource
import org.example.data.storge.audit.AuditDataSourceImpl
import org.example.data.storge.mapper.*
import org.example.data.storge.project.ProjectDataSource
import org.example.data.storge.project.ProjectDataSourceImpl
import org.example.data.storge.task.TaskDataSource
import org.example.data.storge.task.TaskDataSourceImpl
import org.example.data.storge.user.UserDataSource
import org.example.data.storge.user.UserDataSourceImpl
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.ProjectRepository
import org.example.domain.repository.TaskRepository
import org.example.domain.repository.UserRepository
import org.koin.dsl.module
import java.io.File

val appModule = module {

    single<AuditCsvMapper> { AuditCsvMapper() }
    single<ProjectCsvMapper> { ProjectCsvMapper() }
    single<TaskCsvMapper> { TaskCsvMapper(get()) }
    single<StateCsvMapper> { StateCsvMapper() }
    single<UserCsvMapper> { UserCsvMapper() }

    //DataSource
    single<AuditDataSource> {
        AuditDataSourceImpl(
            auditMapper = get<AuditCsvMapper>(),
            csvManager = CsvStorageManager(File(AuditDataSourceImpl.FILE_NAME))
        )
    }

    single<ProjectDataSource> {
        ProjectDataSourceImpl(
            projectMapper = get<ProjectCsvMapper>(),
            stateCsvMapper = get<StateCsvMapper>(),
            csvManager = CsvStorageManager(File(ProjectDataSourceImpl.FILE_NAME))
        )
    }
    single<TaskDataSource> {
        TaskDataSourceImpl(
            taskMapper = get<TaskCsvMapper>(),
            stateCsvMapper = get<StateCsvMapper>(),
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
    single<AuditRepository> { AuditRepositoryImpl(get()) }
    single<ProjectRepository> { ProjectRepositoryImpl(get(), get()) }
    single<TaskRepository> { TaskRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
}