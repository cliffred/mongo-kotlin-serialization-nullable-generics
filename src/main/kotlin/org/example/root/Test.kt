package org.example.root

import com.mongodb.MongoClientSettings
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoClient
import java.io.StringWriter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.bson.BsonWriter
import org.bson.codecs.EncoderContext
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.kotlinx.KotlinSerializerCodec
import org.bson.json.JsonWriter


@Serializable(with = RootPolySerializer::class)
sealed interface Root

@Serializable
data class Foo(
    val str: String,
) : Root

suspend fun main() {
//    codec()
    mongo()
}

private fun codec() {
    val codec = KotlinSerializerCodec.create<Root>()

    val encoderContext: EncoderContext = EncoderContext.builder().build()
    requireNotNull(codec)

    safeEncode("bson") {
        val stringWriter = StringWriter()
        val writer: BsonWriter = JsonWriter(stringWriter)
        codec.encode(writer, it, encoderContext); stringWriter.toString()
    }
}

fun safeEncode(
    type: String,
    encodeFun: (Root) -> Any,
) {
    runCatching { encodeFun(Foo("foo")) }
        .onSuccess { println("$type $it") }
        .onFailure {
            println("FAILURE $type: ${it.stackTraceToString()}")
        }
}

@OptIn(ExperimentalSerializationApi::class)
private suspend fun mongo() {
    val module = SerializersModule {
        polymorphic(Root::class) {
            subclass(Foo::class)
        }
    }

    val rootCodec = KotlinSerializerCodec.create<Root>(
        serializersModule = module,
    )

    val registry = CodecRegistries.fromRegistries(
        CodecRegistries.fromCodecs(rootCodec),
        MongoClientSettings.getDefaultCodecRegistry(),
    )



    // Actually writing to Mongo
    MongoClient.create("mongodb://localhost:27017").use { client ->
        val database = client.getDatabase("mydb").withCodecRegistry(registry)

        val collection = database.getCollection<Root>("containers")
        collection.deleteMany(Filters.empty())

        collection.insertOne(Foo("foo"))

        collection.find().collect {
            println(it)
        }

//        collection.updateOne(
//            Filters.eq("id", 1),
//            Updates.set("box.value", Container(42, Box("foo"))),
//        )
//
//        collection.find().collect {
//            println(it)
//        }
    }
}
