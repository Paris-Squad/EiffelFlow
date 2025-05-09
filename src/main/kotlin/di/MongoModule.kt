package org.example.di

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
import org.koin.core.qualifier.named
import org.koin.dsl.module


val mongoModule = module{
    single { MongoCollections }
    single { MongoConfigProvider() }
    single<MongoDatabase> { get<MongoConfigProvider>().getDatabase() }

    single<AuditRepository>(named("mongoAuditRepo")) {
        MongoAuditRepositoryImpl(
            database = get(),
            taskRepositoryProvider = lazy { get(named("mongoTaskRepo")) }
        )
    }
    single<ProjectRepository>(named("mongoProjectRepo")) {
        MongoProjectRepositoryImpl(
            database = get(),
            auditRepository = get(named("mongoAuditRepo"))
        )
    }
    single<TaskRepository>(named("mongoTaskRepo")) {
        MongoTaskRepositoryImpl(
            database = get(),
            auditRepository = get(named("mongoAuditRepo"))
        )
    }
    single<UserRepository>(named("mongoUserRepo")) {
        MongoUserRepositoryImpl(
            database = get(),
            auditRepository = get(named("mongoAuditRepo"))
        )
    }
    single<AuthRepository>(named("mongoAuthRepo")) {
        MongoAuthRepositoryImpl(
            database = get()
        )
    }
}