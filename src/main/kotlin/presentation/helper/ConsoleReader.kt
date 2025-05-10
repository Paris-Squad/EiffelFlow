package org.example.presentation.io

class ConsoleReader : InputReader {
    override fun readString(): String? {
        return readlnOrNull()
    }
}