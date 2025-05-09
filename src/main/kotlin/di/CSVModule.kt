package org.example.di

import org.example.data.repository.AuditRepositoryImpl
import org.example.data.repository.AuthRepositoryImpl
import org.example.data.repository.ProjectRepositoryImpl
import org.example.data.repository.TaskRepositoryImpl
import org.example.data.repository.UserRepositoryImpl
import org.example.data.storage.FileDataSource
import org.example.data.storage.parser.AuditCsvParser
import org.example.data.storage.parser.ProjectCsvParser
import org.example.data.storage.parser.TaskCsvParser
import org.example.data.storage.parser.UserCsvParser
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.AuthRepository
import org.example.domain.repository.ProjectRepository
import org.example.domain.repository.TaskRepository
import org.example.domain.repository.UserRepository
import org.koin.dsl.module
import java.io.File

val csvModule = module{
    single<AuditRepository> {
        AuditRepositoryImpl(
            auditCsvParser = get<AuditCsvParser>(),
            fileDataSource = FileDataSource(File(AuditRepositoryImpl.FILE_NAME)),
            taskRepository = get()
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