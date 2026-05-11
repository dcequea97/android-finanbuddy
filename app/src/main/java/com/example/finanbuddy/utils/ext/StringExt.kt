package com.example.finanbuddy.utils.ext

fun String.handleTextWithEmoji(): String {
    val name = this // Ejemplo: "🛒Mercado" o "🏠Hogar"

    // Buscamos el índice del primer carácter que sea una letra o un número
    val firstLetterIdentifier = name.indexOfFirst { it.isLetterOrDigit() }

    return if (firstLetterIdentifier != -1) {
        // Tomamos todo lo anterior (emoji) + salto de línea + todo lo posterior (texto)
        val emojiPart = name.take(firstLetterIdentifier).trim()
        val textPart = name.substring(firstLetterIdentifier)

        "$emojiPart\n$textPart"
    } else {
        // Si no hay letras (solo hay un emoji), dejamos el nombre original
        name
    }
}

fun String.extractEmoji(): String {
    val firstLetterIdentifier = this.indexOfFirst { it.isLetterOrDigit() }
    return if (firstLetterIdentifier != -1) {
        this.take(firstLetterIdentifier).trim()
    } else {
        ""
    }
}

fun String.extractText(): String {
    val firstLetterIdentifier = this.indexOfFirst { it.isLetterOrDigit() }
    return if (firstLetterIdentifier != -1) {
        this.substring(firstLetterIdentifier)
    } else {
        ""
    }
}