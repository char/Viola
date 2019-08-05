import codes.som.anthony.viola.*
import codes.som.anthony.viola.binary.ByteArrayInputState
import codes.som.anthony.viola.binary.anyByte
import codes.som.anthony.viola.binary.byteSignature
import codes.som.anthony.viola.charseq.*
import java.io.File.separator
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class BasicTests {
    @Test
    fun parseThreeNumberSequence() {
        val anyInteger = ((char('+') or char('-')).optional then charIn('0' .. '9').repeat.asString) map
                { (sign, num) -> (sign.map(Char::toString).or("") + num).toInt() }
        val separator = (char(',') or char(' ')) thenL char(' ').optionalRepeat

        val parser = parserSequence(
                anyInteger thenL separator,
                anyInteger thenL separator,
                anyInteger thenL separator
        )

        val result = parser(StringInputState("128, +96, -32")).value
        assertEquals(listOf(128, 96, -32), result)
    }

    @Test
    fun parseHelloWorld() {
        val helloWorldParser = str("Hello") thenL char(' ') then str("world!")

        val (failValue, _) = helloWorldParser(StringInputState("Goodbye Moon"))
        assertEquals(null, failValue)

        val (successValue, inputReadToEnd) = helloWorldParser(StringInputState("Hello world!"))
        assertEquals(Pair("Hello", "world!"), successValue)
        assertEquals(null, inputReadToEnd)
    }

    @Test
    fun parseJavaClassVersion() {
        val readShort = anyByte().nTimes(2) map
                { (b1, b2) -> (b1.toInt() shl 8 or b2.toInt()).toShort() }

        val javaVersionParser = byteSignature("CA FE BA BE") thenR readShort then readShort thenL anyByte().repeat

        val objectBuffer = Object::class.java.getResourceAsStream("Object.class").let(InputStream::readBytes)
        val (value, _) = javaVersionParser(ByteArrayInputState(objectBuffer))

        if (value != null) {
            val (minorVersion, majorVersion) = value
            assertEquals(0, minorVersion)
            println("Java object class version: $majorVersion")
        }
    }
}
