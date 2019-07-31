import codes.som.anthony.viola.*
import codes.som.anthony.viola.binary.ByteArrayInputState
import codes.som.anthony.viola.binary.anyByte
import codes.som.anthony.viola.binary.byteSignature
import codes.som.anthony.viola.charseq.StringInputState
import codes.som.anthony.viola.charseq.char
import codes.som.anthony.viola.charseq.str
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class BasicTests {
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
