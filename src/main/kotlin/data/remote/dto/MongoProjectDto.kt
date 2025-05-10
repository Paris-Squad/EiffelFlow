package org.example.data.remote.dto

import org.bson.codecs.pojo.annotations.BsonId

data class MongoProjectDto(
    @BsonId val _id: String,
    val projectName: String,
    val projectDescription: String,
    val createdAt: String,
    val adminId: String,
    val taskStates: List<MongoTaskStateDto>
)
