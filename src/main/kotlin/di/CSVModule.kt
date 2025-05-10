package org.example.di

import org.example.data.repository.*
import org.example.data.storage.FileDataSource
import org.example.data.storage.parser.AuditCsvParser
import org.example.data.storage.parser.ProjectCsvParser
import org.example.data.storage.parser.TaskCsvParser
import org.example.data.storage.parser.UserCsvParser
import org.example.domain.repository.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

val csvModule = module {
    single<AuditRepository>(named("csvAuditRepo")) {
        AuditRepositoryImpl(
            auditCsvParser = get<AuditCsvParser>(),
            fileDataSource = FileDataSource(File(AuditRepositoryImpl.FILE_NAME)),
            taskRepositoryProvider = lazy { get(named("csvTaskRepo")) }
        )
    }
    single<ProjectRepository>(named("csvProjectRepo")) {
        ProjectRepositoryImpl(
            projectCsvParser = get<ProjectCsvParser>(),
            fileDataSource = FileDataSource(File(ProjectRepositoryImpl.FILE_NAME)),
            auditRepository = get(named("csvAuditRepo"))
        )
    }
    single<TaskRepository>(named("csvTaskRepo")) {
        TaskRepositoryImpl(
            taskCsvParser = get<TaskCsvParser>(),
            fileDataSource = FileDataSource(File(TaskRepositoryImpl.FILE_NAME)),
            auditRepository = get(named("csvAuditRepo"))
        )
    }
    single<UserRepository>(named("csvUserRepo")) {
        UserRepositoryImpl(
            userCsvParser = get<UserCsvParser>(),
            fileDataSource = FileDataSource(File(UserRepositoryImpl.FILE_NAME)),
            auditRepository = get(named("csvAuditRepo"))
        )
    }
    single<AuthRepository>(named("csvAuthRepo")) {
        AuthRepositoryImpl(
            authFileDataSource = FileDataSource(File(AuthRepositoryImpl.FILE_NAME)),
            usersFileDataSource =FileDataSource(File(UserRepositoryImpl.FILE_NAME)),
            userCsvParser = get()
        )
    }
}