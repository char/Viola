package codes.som.anthony.viola

inline fun <I, T> Parser<I, T>.andSatisfy(crossinline predicate: (T) -> Boolean) = parser<I, T> { input ->
    val result = this(input)
    val (value, _) = result

    if (value == null || !predicate(value)) return@parser result(null, input)
    else result
}

val <I, T> Parser<I, T>.optional get() = parser<I, Nullable<T>> { input ->
    val (value, next) = this.invoke(input)
    result(Nullable(value), next)
}

val <I, T> Parser<I, T>.optionalRepeat get() = parser<I, List<T>> { input ->
    var currentInput: InputState<I>? = input
    val values = mutableListOf<T>()

    while (currentInput != null) {
        val (value, next) = this(currentInput)
        currentInput = next
        if (value == null)
            break

        values.add(value)
    }

    result(values, currentInput)
}

val <I, T> Parser<I, T>.repeat get() =
    (this then this.optionalRepeat) map { (first, next) -> listOf(first) + next }

inline fun <I, reified T> Parser<I, T>.nTimes(n: Int) = parser<I, Array<T>> { input ->
    val array = arrayOfNulls<T>(n)

    var currentInput: InputState<I>? = input
    for (i in 0 until n) {
        if (currentInput == null) return@parser result(null, input)
        val (value, next) = this(currentInput)
        if (value == null) return@parser result(null, input)

        array[i] = value
        currentInput = next
    }

    @Suppress("UNCHECKED_CAST")
    result(array as Array<T>, currentInput)
}
