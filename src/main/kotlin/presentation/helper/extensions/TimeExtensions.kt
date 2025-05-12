package org.example.presentation.helper.extensions

import kotlinx.datetime.LocalDateTime

fun LocalDateTime.to12HourTimeString(): String {
    val hour24 = this.hour
    val minute = this.minute

    val period = if (hour24 >= 12) "PM" else "AM"

    val hour12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }

    val formattedMinute = minute.toString().padStart(2, '0')

    return "$hour12:$formattedMinute $period"
}

fun LocalDateTime.toFormattedDateTime():String{
    val date = this.date
    val time = this.to12HourTimeString()
    return "$date at $time"
}