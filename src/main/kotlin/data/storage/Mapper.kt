package org.example.data.storage


interface Mapper<From, To> {
    fun mapFrom(input: From): To
    fun mapTo(output: To): From
}