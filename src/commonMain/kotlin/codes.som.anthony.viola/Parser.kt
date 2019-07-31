@file:Suppress("NOTHING_TO_INLINE")
package codes.som.anthony.viola

typealias Parser<I, T> = (InputState<I>) -> Pair<T?, InputState<I>?>

inline fun <I, T> parser(noinline parser: Parser<I, T>): Parser<I, T> = parser

inline fun <I, T> result(value: T?, input: InputState<I>?) =
    Pair(value, input)
