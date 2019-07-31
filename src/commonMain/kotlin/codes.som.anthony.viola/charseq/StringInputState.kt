package codes.som.anthony.viola.charseq

import codes.som.anthony.viola.InputState

data class StringInputState(private val str: String, private val index: Int = 0) : InputState<Char> {
    override fun advance(): Pair<Char, InputState<Char>?> =
        Pair(str[index],
                if (index == str.lastIndex) null
                else StringInputState(str, index + 1))
}
