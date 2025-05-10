package org.example.di

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import data.mongorepository.*
import org.example.data.remote.MongoCollections
import org.example.data.remote.MongoConfigProvider
import org.example.domain.repository.*
import org.koin.dsl.module

val mongoModule = module {
    single { MongoCollections }
    single { MongoConfigProvider() }
    single<MongoClient> { get<MongoConfigProvider>().getMongoClient() }
    single<MongoDatabase> { get<MongoConfigProvider>().getDatabase(mongoClient = get()) }


    single<AuditRepository> {
        MongoAuditRepositoryImpl(
            database = get(),
            taskRepositoryProvider = lazy { get() },
            auditLogMapper = get()
        )
    }
    single<ProjectRepository> {
        MongoProjectRepositoryImpl(
            database = get(),
            projectMapper = get()
        )
    }
    single<TaskRepository> {
        MongoTaskRepositoryImpl(
            database = get(),
            auditRepository = get(),
            taskMapper = get()
        )
    }
    single<UserRepository> {
        MongoUserRepositoryImpl(
            database = get(),
            auditRepository = get(),
            userMapper = get()
        )
    }
    single<AuthRepository> {
        MongoAuthRepositoryImpl(
            database = get(),
            userMapper = get()
        )
    }
}