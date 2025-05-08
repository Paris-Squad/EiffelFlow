package org.example.presentation.presenter.io

class ConsolePrinter : Printer {

    override fun displayLn(input: Any?) {
        println(input)
    }
}