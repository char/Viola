package codes.som.anthony.viola.binary

import codes.som.anthony.viola.map
import codes.som.anthony.viola.parser
import codes.som.anthony.viola.parserSequence
import codes.som.anthony.viola.result

fun anyByte() = parser<Byte, Byte> { input ->
    val (byte, next) = input.advance()
    result(byte, next)
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
