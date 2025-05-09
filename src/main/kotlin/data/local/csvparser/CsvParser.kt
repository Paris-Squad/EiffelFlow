package org.example.data.local.csvparser

interface CsvParser<T> {
    fun parseCsvLine(csvLine: String): T
    fun serialize(item: T): String
}