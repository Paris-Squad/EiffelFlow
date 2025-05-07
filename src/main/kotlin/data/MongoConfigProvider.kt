package org.example.data

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase

class MongoConfigProvider {
    fun getDatabase(): MongoDatabase {
        return MongoClient.create(CONNECTION_STRING_URI_PLACEHOLDER)
            .getDatabase(DATABASE_NAME)
    }

    companion object {
        private const val CONNECTION_STRING_URI_PLACEHOLDER =
            "mongodb+srv://abdelrahmanraafaat:5PcgrHNqtPP4Cdez@cluster0.bekcque.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
        private const val DATABASE_NAME = "eiffle_flow"
    }
}