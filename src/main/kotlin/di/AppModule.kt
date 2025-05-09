package org.example.di

import org.example.data.migration.AuditLogsCsvToMongoMigrator
import org.example.data.migration.ProjectCsvToMongoMigrator
import org.example.data.migration.TaskCsvToMongoMigrator
import org.example.data.migration.UserCsvToMongoMigrator
import org.example.data.storage.parser.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    single<AuditCsvParser> { AuditCsvParser() }
    single<ProjectCsvParser> { ProjectCsvParser(get()) }
    single<TaskCsvParser> { TaskCsvParser(get()) }
    single<StateCsvParser> { StateCsvParser() }
    single<UserCsvParser> { UserCsvParser() }


    single<UserCsvToMongoMigrator> {
        UserCsvToMongoMigrator(
            csvRepository = get(named("csvUserRepo")),
            mongoRepository = get(named("mongoUserRepo"))
        )
    }

    single<TaskCsvToMongoMigrator> {
        TaskCsvToMongoMigrator(
            csvRepository = get(named("csvTaskRepo")),
            mongoRepository = get(named("mongoTaskRepo"))
        )
    }

    single<ProjectCsvToMongoMigrator> {
        ProjectCsvToMongoMigrator(
            csvRepository = get(named("csvProjectRepo")),
            mongoRepository = get(named("mongoProjectRepo"))
        )
    }

    single<AuditLogsCsvToMongoMigrator> {
        AuditLogsCsvToMongoMigrator(
            csvRepository = get(named("csvAuditRepo")),
            mongoRepository = get(named("mongoAuditRepo"))
        )
    }


}