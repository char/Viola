import codes.som.anthony.viola.*
import codes.som.anthony.viola.binary.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream


sealed class ConstantPoolEntry

class ConstantPoolUTF(val value: String) : ConstantPoolEntry()
class ConstantPoolInt(val value: Int) : ConstantPoolEntry()
class ConstantPoolFloat(val value: Float) : ConstantPoolEntry()
class ConstantPoolLong(val value: Long) : ConstantPoolEntry()
class ConstantPoolDouble(val value: Double) : ConstantPoolEntry()

class ConstantPoolString(val index: Int) : ConstantPoolEntry()

class ConstantPoolClass(val nameIndex: Int) : ConstantPoolEntry()
class ConstantPoolFieldReference(val classIndex: Int, val nameAndTypeIndex: Int) : ConstantPoolEntry()
class ConstantPoolMethodReference(val classIndex: Int, val nameAndTypeIndex: Int) : ConstantPoolEntry()
class ConstantPoolInterfaceMethodReference(val classIndex: Int, val nameAndTypeIndex: Int) : ConstantPoolEntry()
class ConstantPoolNameAndTypeReference(val nameIndex: Int, val descriptorIndex: Int) : ConstantPoolEntry()

class JVMBytecodeParsing {
    @Test
    fun parseObject() {
        val classBuffer = Object::class.java.getResourceAsStream("Object.class").readBytes()

        class ClassFileParser {

            val uint16 = nBytes(2) map { (higher, lower) -> ((higher.toInt() and 255) shl 8) or (lower.toInt() and 255) }
            val int32 = nBytes(4) map { (b1, b2, b3, b4) ->
                ((b1.toInt() and 255) shl 24) or
                        ((b2.toInt() and 255) shl 16) or
                        ((b3.toInt() and 255) shl 8) or
                        ((b4.toInt() and 255))
            }

            val int64 = nBytes(8) map {
                var total = 0L
                for (i in 0 until 8) {
                    total = total or ((it[i].toLong() and 255) shl (64 - 8 * i))
                }

                total
            }

            val magic = byteSignature("CA FE BA BE")

            val constantPoolEntry = anyByte() thenUseR { tag ->
                when (tag.toInt()) {
                    1 -> uint16 thenUseR { len ->
                        nBytes(len) map { buf ->
                            val bos = ByteArrayOutputStream()
                            DataOutputStream(bos).use {
                                it.writeShort(len)
                                it.write(buf)
                            }

                            val dataInputStream = DataInputStream(ByteArrayInputStream(bos.toByteArray()))
                            val utf = dataInputStream.readUTF()
                            ConstantPoolUTF(utf)
                        }
                    }

                    3 -> int32 map(::ConstantPoolInt)
                    4 -> int32 map { ConstantPoolFloat(Float.fromBits(it)) }

                    5 -> int64 map(::ConstantPoolLong)
                    6 -> int64 map { ConstantPoolDouble(Double.fromBits(it)) }

                    7 -> uint16 map(::ConstantPoolClass)
                    8 -> uint16 map(::ConstantPoolString)
                    
                    9 -> uint16 then uint16 map { (classIndex, nameAndTypeIndex) ->
                        ConstantPoolFieldReference(classIndex, nameAndTypeIndex)
                    }
                    10 -> uint16 then uint16 map { (classIndex, nameAndTypeIndex) ->
                        ConstantPoolMethodReference(classIndex, nameAndTypeIndex)
                    }
                    11 -> uint16 then uint16 map { (classIndex, nameAndTypeIndex) ->
                        ConstantPoolInterfaceMethodReference(classIndex, nameAndTypeIndex)
                    }

                    12 -> uint16 then uint16 map { (nameIndex, descriptorIndex) ->
                        ConstantPoolNameAndTypeReference(nameIndex, descriptorIndex)
                    }

                    else -> reject()
                }
            } // TODO

            val constantPool = uint16 thenUseR { n -> parserSequence(*Array(n - 1, { constantPoolEntry })) }
            val interfaces = uint16 thenUseR { n -> parserSequence(*Array(n, { uint16 })) }

            val classFile = parserSequence(
                    magic,
                    uint16, // minor_version
                    uint16, // major_version
                    constantPool,
                    uint16, // access
                    uint16, // this_class
                    uint16, // super_class
                    interfaces
            )
        }

        val (result) = ClassFileParser().classFile(ByteArrayInputState(classBuffer))
        println(result)
    }
}
