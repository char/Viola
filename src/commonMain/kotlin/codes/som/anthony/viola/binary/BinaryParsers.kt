package codes.som.anthony.viola.binary

import codes.som.anthony.viola.*

fun anyByte() = parser<Byte, Byte> { input ->
    val (byte, next) = input.advance()
    result(byte, next)
}

fun nBytes(n: Int) = parser<Byte, ByteArray> { input ->
    val array = ByteArray(n)

    var currentInput: InputState<Byte>? = input
    for (i in array.indices) {
        if (currentInput == null) return@parser result(null, input)
        val (byte, next) = currentInput.advance()
        array[i] = byte
        currentInput = next
    }

    result(array, currentInput)
}

fun byte(b: Byte) = parser<Byte, Byte> { input ->
    val (byte, next) = input.advance()
    if (byte == b) result(byte, next)
    else result(null, input)
}

fun byteSignature(descriptor: String) =
        parserSequence(*descriptor.split(" ").map {
            when (it) {
                "??" -> anyByte()
                else -> byte(it.toShort(16).toByte())
            }
        }.toTypedArray()) map { it.toByteArray() }
