package org.example.di

import org.example.data.repository.*
import org.example.data.storage.FileDataSource
import org.example.data.storage.parser.AuditCsvParser
import org.example.data.storage.parser.ProjectCsvParser
import org.example.data.storage.parser.TaskCsvParser
import org.example.data.storage.parser.UserCsvParser
import org.example.domain.repository.*
import org.koin.dsl.module
import java.io.File

val csvModule = module {
    single<AuditRepository> {
        AuditRepositoryImpl(
            auditCsvParser = get<AuditCsvParser>(),
            fileDataSource = FileDataSource(File(AuditRepositoryImpl.FILE_NAME)),
            taskRepositoryProvider = lazy { get() }
        )
    }
    single<ProjectRepository> {
        ProjectRepositoryImpl(
            projectCsvParser = get<ProjectCsvParser>(),
            fileDataSource = FileDataSource(File(ProjectRepositoryImpl.FILE_NAME))
        )
    }
    single<TaskRepository> {
        TaskRepositoryImpl(
            taskCsvParser = get<TaskCsvParser>(),
            fileDataSource = FileDataSource(File(TaskRepositoryImpl.FILE_NAME)),
            auditRepository = get()
        )
    }
    single<UserRepository> {
        UserRepositoryImpl(
            userCsvParser = get<UserCsvParser>(),
            fileDataSource = FileDataSource(File(UserRepositoryImpl.FILE_NAME)),
            auditRepository = get()
        )
    }
    single<AuthRepository> {
        AuthRepositoryImpl(
            authFileDataSource = FileDataSource(File(AuthRepositoryImpl.FILE_NAME)),
            usersFileDataSource =FileDataSource(File(UserRepositoryImpl.FILE_NAME)),
            userCsvParser = get()
        )
    }
}