package com.example.finanbuddy.utils.ext

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun LocalDate?.defaultFormat(): String {
    if (this == null) return "Select a date"
    val formatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy", Locale("es", "ES"))
    return this.format(formatter)
}

fun LocalTime?.defaultFormat(): String {
    if (this == null) return "Select a time"
    // Formato de 12 horas con AM/PM (ej: 07:30 PM)
    val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    return this.format(formatter)
}