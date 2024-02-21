package org.example

import com.mongodb.internal.connection.ByteBufferBsonOutput
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.serialization.Serializable
import org.bson.BsonBinaryWriter
import org.bson.BsonWriter
import org.bson.ByteBufNIO
import org.bson.codecs.EncoderContext
import org.bson.codecs.kotlinx.KotlinSerializerCodec
import java.nio.ByteBuffer

@Serializable
data class Sample<T>(
    val value: T,
)

@Serializable
data class Container(
    // Using a nullable generic causes the exception, Sample<String> works fine
    val sample: Sample<String?>,
)

suspend fun main() {
    val containerFoo = Container(Sample("foo"))
    val containerNull = Container(Sample(null))

    // Using the codec directly
    val codec = KotlinSerializerCodec.create<Container>()!!
    val byteBuffer = ByteBuffer.allocate(64)
    val byteBufferBsonOutput = ByteBufferBsonOutput { ByteBufNIO(byteBuffer) }
    val writer: BsonWriter = BsonBinaryWriter(byteBufferBsonOutput)
    val encoderContext: EncoderContext = EncoderContext.builder().build()
    codec.encode(writer, containerFoo, encoderContext)
    codec.encode(writer, containerNull, encoderContext)

    // Actually writing to Mongo
    MongoClient.create("mongodb://localhost:27017").use { client ->
        val database = client.getDatabase("mydb")
        val collection = database.getCollection<Container>("containers")

        collection.insertOne(containerFoo)
        collection.insertOne(containerNull)

        collection.find().collect {
            println(it)
        }
    }
}
