package org.example.presentation.io

class ConsolePrinter : Printer {

    override fun displayLn(input: Any?) {
        println(input)
    }
}