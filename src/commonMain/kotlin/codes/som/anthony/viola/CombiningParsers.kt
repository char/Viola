package codes.som.anthony.viola

inline infix fun <I, A, B> Parser<I, A>.then(crossinline other: Parser<I, B>) = parser<I, Pair<A, B>> { input ->
    val (valueA, nextA) = this(input)
    if (valueA == null) return@parser result(null, input)

    if (nextA == null) return@parser result(null, input)
    val (valueB, nextB) = other(nextA)
    if (valueB == null) return@parser result(null, input)

    result(Pair(valueA, valueB), nextB)
}

inline infix fun <I, A, B> Parser<I, A>.thenL(crossinline other: Parser<I, B>) = (this then other) map { it?.first }
inline infix fun <I, A, B> Parser<I, A>.thenR(crossinline other: Parser<I, B>) = (this then other) map { it?.second }

fun <I> parserSequence(vararg parsers: Parser<I, *>) = parser<I, List<Any>> { input ->
    val list = mutableListOf<Any>()

    var currentInput: InputState<I>? = input
    for (parser in parsers) {
        if (currentInput == null) return@parser result(null, input)

        val (value, next) = parser(currentInput)
        if (value == null) return@parser result(null, input)
        list.add(value)

        currentInput = next
    }

    return@parser result(list, currentInput)
}
