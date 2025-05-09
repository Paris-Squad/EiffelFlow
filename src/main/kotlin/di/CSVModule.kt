package org.example.di

import org.example.data.local.csvrepository.CsvAuditRepositoryImpl
import org.example.data.local.csvrepository.CsvAuthRepositoryImpl
import org.example.data.local.csvrepository.CsvProjectRepositoryImpl
import org.example.data.local.csvrepository.CsvTaskRepositoryImpl
import org.example.data.local.csvrepository.CsvUserRepositoryImpl
import org.example.data.local.FileDataSource
import org.example.data.local.parser.AuditCsvParser
import org.example.data.local.parser.ProjectCsvParser
import org.example.data.local.parser.TaskCsvParser
import org.example.data.local.parser.UserCsvParser
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