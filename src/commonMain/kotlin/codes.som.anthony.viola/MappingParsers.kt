package codes.som.anthony.viola

inline infix fun <I, A, B> Parser<I, A>.map(crossinline mapper: (A) -> B?) = parser<I, B> { input ->
    val (value, next) = this(input)
    if (value == null) return@parser result(null, input)

    result(mapper(value), next)
}
