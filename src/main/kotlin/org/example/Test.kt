package org.example

import java.io.StringWriter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import org.bson.BsonWriter
import org.bson.codecs.EncoderContext
import org.bson.codecs.kotlinx.KotlinSerializerCodec
import org.bson.json.JsonWriter

@Serializable
data class Container(
    // Using a nullable generic causes the exception, Box<String> works fine
    val box: Box<String?>,
)

@OptIn(ExperimentalSerializationApi::class)
fun main() {
    val codec = KotlinSerializerCodec.create<Container>()!!
    val encoderContext: EncoderContext = EncoderContext.builder().build()
//    val byteBuffer = ByteBuffer.allocate(128)
//    val byteBufferBsonOutput = ByteBufferBsonOutput { ByteBufNIO(byteBuffer) }


//    safeEncode("json") { Json.encodeToString(it) }
//    safeEncode("protobuf") { ProtoBuf.encodeToHexString(it) }
//    safeEncode("hocon") { Hocon.encodeToConfig(it) }
//    safeEncode("cbor") { Cbor.encodeToHexString(it) }
//    safeEncode("properties") { Properties.encodeToStringMap(it) }
    safeEncode("bson") {
        val stringWriter = StringWriter()
        val writer: BsonWriter = JsonWriter(stringWriter)
        codec.encode(writer, it, encoderContext); stringWriter.toString()
    }
}

fun safeEncode(
    type: String,
    encodeFun: (Container) -> Any,
) {
    listOf(
        Container(Box("String")),
        Container(Box(null)),
    ).forEach {
        runCatching { encodeFun(it) }
            .onSuccess { println("$type $it") }
            .onFailure {
                println("FAILURE $type: ${it.stackTraceToString()}")
            }
    }
}

@OptIn(ExperimentalSerializationApi::class)
suspend fun mongo() {
    // Using the codec directly
//    val codec = KotlinSerializerCodec.create<Container>()!!
//    val byteBuffer = ByteBuffer.allocate(128)
//    val byteBufferBsonOutput = ByteBufferBsonOutput { ByteBufNIO(byteBuffer) }
//    val writer: BsonWriter = BsonBinaryWriter(byteBufferBsonOutput)
//    val encoderContext: EncoderContext = EncoderContext.builder().build()
//    codec.encode(writer, containerFoo, encoderContext)
//    codec.encode(writer, containerNull, encoderContext)

    // Actually writing to Mongo
//    MongoClient.create("mongodb://localhost:27017").use { client ->
//        val database = client.getDatabase("mydb")
//
//        val collection = database.getCollection<Container>("containers")
//        collection.deleteMany(Filters.empty())
//
//        collection.insertOne(containerFoo)
//        collection.insertOne(containerNull)
//
//        collection.find().collect {
//            println(it)
//        }
//
//        collection.updateOne(
//            Filters.eq("id", 1),
//            Updates.set("box.value", Container(42, Box("foo"))),
//        )
//
//        collection.find().collect {
//            println(it)
//        }
//    }
}
