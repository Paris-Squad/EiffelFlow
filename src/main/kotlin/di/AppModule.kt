package org.example.di

import org.example.data.repository.*
import org.example.data.storage.FileDataSource
import org.example.data.storage.parser.*
import org.example.domain.repository.*
import org.koin.dsl.module
import java.io.File

val appModule = module {

    single<AuditCsvParser> { AuditCsvParser() }
    single<ProjectCsvParser> { ProjectCsvParser(get()) }
    single<TaskCsvParser> { TaskCsvParser(get()) }
    single<StateCsvParser> { StateCsvParser() }
    single<UserCsvParser> { UserCsvParser() }

    //repose
    single<AuditRepository> {
        AuditRepositoryImpl(
            auditCsvParser = get<AuditCsvParser>(),
            fileDataSource = FileDataSource(File(AuditRepositoryImpl.FILE_NAME))
        )
    }
    single<ProjectRepository> {
        ProjectRepositoryImpl(
            projectCsvParser = get<ProjectCsvParser>(),
            fileDataSource = FileDataSource(File(ProjectRepositoryImpl.FILE_NAME)),
            auditRepository = get()
        )
    }
    single<TaskRepository> {
        TaskRepositoryImpl(
            taskCsvParser = get<TaskCsvParser>(),
            fileDataSource = FileDataSource(File(TaskRepositoryImpl.FILE_NAME)),
            auditRepository = get())
    }
    single<UserRepository> {
        UserRepositoryImpl(
            userCsvParser = get<UserCsvParser>(),
            fileDataSource = FileDataSource(File(UserRepositoryImpl.FILE_NAME)),
            auditRepository = get()
        )
    }
    single<AuthRepository> {
        AuthRepositoryImpl(FileDataSource(File(AuthRepositoryImpl.FILE_NAME)) , get())
    }
}