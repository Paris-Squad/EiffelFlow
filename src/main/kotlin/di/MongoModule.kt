package org.example.di

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.example.data.remote.MongoCollections
import org.example.data.remote.DataBaseConfigs
import org.koin.dsl.module

val dataBaseModule = module {
    single { MongoCollections }
    single { DataBaseConfigs() }
    single<MongoClient> { get<DataBaseConfigs>().getMongoClient() }
    single<MongoDatabase> { get<DataBaseConfigs>().getDatabase(mongoClient = get()) }
}