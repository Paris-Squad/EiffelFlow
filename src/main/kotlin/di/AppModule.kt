package org.example.di

import org.example.data.storage.parser.*
import org.koin.dsl.module

val appModule = module {

    single<AuditCsvParser> { AuditCsvParser() }
    single<ProjectCsvParser> { ProjectCsvParser(get()) }
    single<TaskCsvParser> { TaskCsvParser(get()) }
    single<StateCsvParser> { StateCsvParser() }
    single<UserCsvParser> { UserCsvParser() }
}