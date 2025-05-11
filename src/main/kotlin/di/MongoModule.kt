package org.example.di

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.example.data.remote.MongoDatabaseProvider
import org.example.data.remote.MongoClientProvider
import org.koin.dsl.module

val dataBaseModule = module {
    single<MongoClient> { MongoClientProvider().client }
    single<MongoDatabase> { MongoDatabaseProvider(get()).database }
}