package org.example.data.storge


interface Mapper<From, To> {
    fun mapFrom(input: From): To
    fun mapTo(output: To): From
}