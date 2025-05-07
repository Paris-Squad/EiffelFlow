package org.example.di

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import data.mongorepository.MongoAuditRepositoryImpl
import data.mongorepository.MongoAuthRepositoryImpl
import data.mongorepository.MongoProjectRepositoryImpl
import data.mongorepository.MongoTaskRepositoryImpl
import data.mongorepository.MongoUserRepositoryImpl
import org.example.data.MongoCollections
import org.example.data.MongoConfigProvider
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.AuthRepository
import org.example.domain.repository.ProjectRepository
import org.example.domain.repository.TaskRepository
import org.example.domain.repository.UserRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val mongoModule = module{
    // MongoDatabase setup (adjust connection details as needed)
    single<MongoDatabase> {
        MongoClient.create("mongodb://localhost:27017")
            .getDatabase("your-database-name")
    }
    single { MongoCollections }
    single { MongoConfigProvider }
//    single { get<MongoConfigProvider>().getMongoConfig() }
    single<MongoDatabase> { get<MongoConfigProvider>().getDatabase() }


    single<AuditRepository> {
        MongoAuditRepositoryImpl(
            database = get(),
            taskRepository = get()
        )
    }
    single<ProjectRepository> {
        MongoProjectRepositoryImpl(
            database = get(),
            auditRepository = get()
        )
    }
    single<TaskRepository> {
        MongoTaskRepositoryImpl(
            database = get(),
            auditRepository = get()
        )
    }
    single<UserRepository> {
        MongoUserRepositoryImpl(
            database = get(),
            auditRepository = get()
        )
    }
    single<AuthRepository> {
        MongoAuthRepositoryImpl(
            database = get()
        )
    }
}