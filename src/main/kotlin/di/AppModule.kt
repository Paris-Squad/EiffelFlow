package org.example.di

import org.example.data.local.parser.AuditCsvParser
import org.example.data.local.parser.ProjectCsvParser
import org.example.data.local.parser.StateCsvParser
import org.example.data.local.parser.TaskCsvParser
import org.example.data.local.parser.UserCsvParser
import org.example.data.remote.mapper.UserMapper
import org.koin.dsl.module

val appModule = module {

    single<AuditCsvParser> { AuditCsvParser() }
    single<ProjectCsvParser> { ProjectCsvParser(get()) }
    single<TaskCsvParser> { TaskCsvParser(get()) }
    single<StateCsvParser> { StateCsvParser() }
    single<UserCsvParser> { UserCsvParser() }
    single<UserMapper> { UserMapper() }
}