package codes.som.anthony.viola

data class Maybe<T>(val value: T?) {
    fun <U> map(mapper: (T) -> U) = Maybe<U>(value?.let(mapper))

    fun or(other: T) = value ?: other
}
