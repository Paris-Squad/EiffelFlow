package org.example.di

import org.example.data.local.CsvAuditRepositoryImpl
import org.example.data.local.CsvAuthRepositoryImpl
import org.example.data.local.CsvProjectRepositoryImpl
import org.example.data.local.CsvTaskRepositoryImpl
import org.example.data.local.CsvUserRepositoryImpl
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
            auditRepository = get()
        )
    }
    single<TaskRepository> {
        CsvTaskRepositoryImpl(
            taskCsvParser = get<TaskCsvParser>(),
            fileDataSource = FileDataSource(File(CsvTaskRepositoryImpl.FILE_NAME)),
            auditRepository = get()
        )
    }
    single<UserRepository> {
        CsvUserRepositoryImpl(
            userCsvParser = get<UserCsvParser>(),
            fileDataSource = FileDataSource(File(CsvUserRepositoryImpl.FILE_NAME)),
            auditRepository = get()
        )
    }
    single<AuthRepository> {
        CsvAuthRepositoryImpl(
            authFileDataSource = FileDataSource(File(CsvAuthRepositoryImpl.FILE_NAME)),
            usersFileDataSource = FileDataSource(File(CsvUserRepositoryImpl.FILE_NAME)),
            userCsvParser = get()
        )
    }
}