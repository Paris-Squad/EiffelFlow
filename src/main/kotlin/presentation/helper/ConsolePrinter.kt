package org.example.presentation.helper

import org.example.presentation.io.Printer

class ConsolePrinter : Printer {

    override fun displayLn(input: Any?) {
        println(input)
    }
}