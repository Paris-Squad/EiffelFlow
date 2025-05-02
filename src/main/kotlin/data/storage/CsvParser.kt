package org.example.data.storage


interface CsvParser<T> {
    fun parseCsvLine(csvLine: String): T
    fun serialize(item: T): String
}