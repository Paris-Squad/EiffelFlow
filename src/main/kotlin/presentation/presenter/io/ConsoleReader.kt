package org.example.presentation.presenter.io

class ConsoleReader : InputReader {
    override fun readString(): String? {
        return readlnOrNull()
    }
}