package org.example.data.remote.dto

import org.bson.codecs.pojo.annotations.BsonId

data class MongoUserDto(
    @BsonId val _id: String,
    val username: String,
    val password: String,
    val role: String
)
