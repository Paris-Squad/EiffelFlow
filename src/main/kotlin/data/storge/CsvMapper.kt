package org.example.data.storge

interface CsvMapper<T> {
    fun fromCsv(columns: List<String>): T
    fun toCsv(entity: T): List<String>
}