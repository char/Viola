package codes.som.anthony.viola

inline infix fun <I, A, B> Parser<I, A>.then(crossinline other: Parser<I, B>) = parser<I, Pair<A, B>> { input ->
    val (valueA, nextA) = this(input)
    if (valueA == null || nextA == null) return@parser result(null, input)

    val (valueB, nextB) = other(nextA)
    if (valueB == null) return@parser result(null, input)

    result(Pair(valueA, valueB), nextB)
}

inline infix fun <I, A, B> Parser<I, A>.thenL(crossinline other: Parser<I, B>) = (this then other) map { it.first }
inline infix fun <I, A, B> Parser<I, A>.thenR(crossinline other: Parser<I, B>) = (this then other) map { it.second }

inline infix fun <I, A, B> Parser<I, A>.thenUse(crossinline parserGenerator: (A) -> Parser<I, B>) = parser<I, Pair<A, B>> { input ->
    val (valueA, nextA) = this(input)
    if (valueA == null || nextA == null) return@parser result(null, input)

    val parserB = parserGenerator(valueA)

    val (valueB, nextB) = parserB(nextA)
    if (valueB == null) return@parser result(null, input)

    result(Pair(valueA, valueB), nextB)
}

inline infix fun <I, A, B> Parser<I, A>.thenUseR(crossinline parserGenerator: (A) -> Parser<I, B>) = (this thenUse parserGenerator) map { it.second }

inline infix fun <I, T> Parser<I, T>.or(crossinline other: Parser<I, T>) = parser<I, T> { input ->
    val (valueA, nextA) = this(input)
    if (valueA != null) return@parser result(valueA, nextA)

    val (valueB, nextB) = other(input)
    if (valueB != null) return@parser result(valueB, nextB)

    return@parser result(null, input)
}

inline fun <I, T> not(crossinline parser: Parser<I, T>) = parser<I, I> { input ->
    val (value, _) = parser(input)
    if (value != null) return@parser result(null, input)

    val (result, next) = input.advance()
    result(result, next)
}

fun <I, R> parserSequence(vararg parsers: Parser<I, out R>) = parser<I, List<R>> { input ->
    val list = mutableListOf<R>()

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
