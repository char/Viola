package codes.som.anthony.viola.binary

import codes.som.anthony.viola.InputState

class ByteArrayInputState(private val array: ByteArray, private val index: Int = 0) : InputState<Byte> {
    override fun advance() =
            Pair(array[index],
                    if (index == array.lastIndex) null
                    else ByteArrayInputState(array, index + 1))
}
