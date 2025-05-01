package org.example.di

import org.example.data.repository.AuditRepositoryImpl
import org.example.data.repository.AuthRepositoryImpl
import org.example.data.repository.ProjectRepositoryImpl
import org.example.data.repository.TaskRepositoryImpl
import org.example.data.repository.UserRepositoryImpl
import org.example.data.storage.FileStorageManager
import org.example.data.storage.audit.AuditDataSource
import org.example.data.storage.audit.AuditDataSourceImpl
import org.example.data.storage.auth.AuthDataSource
import org.example.data.storage.auth.AuthDataSourceImpl
import org.example.data.storage.mapper.*
import org.example.data.storage.project.ProjectDataSource
import org.example.data.storage.project.ProjectDataSourceImpl
import org.example.data.storage.task.TaskDataSource
import org.example.data.storage.task.TaskDataSourceImpl
import org.example.data.storage.user.UserDataSource
import org.example.data.storage.user.UserDataSourceImpl
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.AuthRepository
import org.example.domain.repository.ProjectRepository
import org.example.domain.repository.TaskRepository
import org.example.domain.repository.UserRepository
import org.koin.dsl.module
import java.io.File

val appModule = module {

    single<AuditCsvMapper> { AuditCsvMapper() }
    single<ProjectCsvMapper> { ProjectCsvMapper(get()) }
    single<TaskCsvMapper> { TaskCsvMapper(get()) }
    single<StateCsvMapper> { StateCsvMapper() }
    single<UserCsvMapper> { UserCsvMapper() }

    //DataSource
    single<AuditDataSource> {
        AuditDataSourceImpl(
            auditMapper = get<AuditCsvMapper>(),
            csvManager = FileStorageManager(File(AuditDataSourceImpl.FILE_NAME))
        )
    }

    single<ProjectDataSource> {
        ProjectDataSourceImpl(
            projectMapper = get<ProjectCsvMapper>(),
            stateCsvMapper = get<StateCsvMapper>(),
            csvManager = FileStorageManager(File(ProjectDataSourceImpl.FILE_NAME))
        )
    }
    single<TaskDataSource> {
        TaskDataSourceImpl(
            taskMapper = get<TaskCsvMapper>(),
            stateCsvMapper = get<StateCsvMapper>(),
            csvManager = FileStorageManager(File(TaskDataSourceImpl.FILE_NAME))
        )
    }

    single<UserDataSource> {
        UserDataSourceImpl(
            userMapper = get<UserCsvMapper>(),
            csvManager = FileStorageManager(File(UserDataSourceImpl.FILE_NAME))
        )
    }

    single<AuthDataSource> {
        AuthDataSourceImpl(FileStorageManager(File(AuthDataSourceImpl.FILE_NAME)))
    }

    //repose
    single<AuditRepository> { AuditRepositoryImpl(get()) }
    single<ProjectRepository> { ProjectRepositoryImpl(get(), get()) }
    single<TaskRepository> { TaskRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
}