package org.example.root

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

class RootPolySerializer : KSerializer<Root> {

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(
        encoder: Encoder,
        value: Root,
    ) {
        val actualSerializer = encoder.serializersModule.getPolymorphic(Root::class, value)
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, actualSerializer!!.descriptor.serialName)
            encodeSerializableElement(descriptor, 1, actualSerializer!!, value)
        }
    }

    override val descriptor: SerialDescriptor
        get() = PolymorphicSerializer(Root::class).descriptor

    override fun deserialize(
        decoder: Decoder,
    ): Root = decoder.decodeStructure(descriptor) {
        var klassName: String? = null
        var value: Any? = null

        mainLoop@ while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                CompositeDecoder.DECODE_DONE -> {
                    break@mainLoop
                }

                0 -> {
                    // ignore as json elemnent
                }

                1 -> {
                    klassName = decodeStringElement(descriptor, index)
                }

                2 -> {
                    klassName = requireNotNull(klassName) { "Cannot read polymorphic value before its type token" }
                    val serializer =
                        decoder.serializersModule.getPolymorphic(Root::class, klassName)
                    value = decodeSerializableElement(descriptor, index, serializer!!)
                }

                else -> throw SerializationException(
                    "Invalid index in polymorphic deserialization of " +
                            (klassName ?: "unknown class") +
                            "\n Expected 0, 1 or DECODE_DONE(-1), but found $index",
                )
            }
        }
        @Suppress("UNCHECKED_CAST")
        requireNotNull(value) { "Polymorphic value has not been read for class $klassName" } as Root
    }
}