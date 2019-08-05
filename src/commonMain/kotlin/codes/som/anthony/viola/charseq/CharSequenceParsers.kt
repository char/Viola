package codes.som.anthony.viola.charseq

import codes.som.anthony.viola.*

fun char(c: Char) = parser<Char, Char> { input ->
    val (char, next) = input.advance()

    if (c == char) result(char, next)
    else result(null, input)
}

fun str(s: String) = parser<Char, String> { input ->
    var currentInput: InputState<Char>? = input

    for (c in s) {
        if (currentInput == null) return@parser result(null, input)
        val (char, next) = currentInput.advance()
        if (char != c) return@parser result(null, input)
        currentInput = next
    }

    return@parser result(s, currentInput)
}

fun anyChar() = parser<Char, Char> { input ->
    val (char, next) = input.advance()
    result(char, next)
}

fun charIn(s: String) =
        anyChar().andSatisfy { it in s }

fun charIn(range: CharRange) =
        anyChar().andSatisfy { it in range }

val Parser<Char, Iterable<Char>>.asString
    get() = this map { it.joinToString("") }
