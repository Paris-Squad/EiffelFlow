package org.example.di

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import data.mongorepository.MongoAuditRepositoryImpl
import data.mongorepository.MongoAuthRepositoryImpl
import data.mongorepository.MongoProjectRepositoryImpl
import data.mongorepository.MongoTaskRepositoryImpl
import data.mongorepository.MongoUserRepositoryImpl
import org.example.data.remote.MongoCollections
import org.example.data.remote.MongoConfigProvider
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.AuthRepository
import org.example.domain.repository.ProjectRepository
import org.example.domain.repository.TaskRepository
import org.example.domain.repository.UserRepository
import org.koin.dsl.module

val mongoModule = module{
    single { MongoCollections }
    single { MongoConfigProvider() }
    single<MongoDatabase> { get<MongoConfigProvider>().getDatabase() }


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