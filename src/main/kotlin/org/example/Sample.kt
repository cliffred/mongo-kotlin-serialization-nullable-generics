package org.example

import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Sample<T>(
    val value: T,
)

@Serializable
data class Container(
    val sample: Sample<String?>,
)

suspend fun main() {
    val data = Container(Sample(null))
    json(data)
    mongo(data)
}

private fun json(data: Container) {
    val string = Json.encodeToString(data)
    println(string)

    val obj = Json.decodeFromString<Container>(string)
    println(obj)
}

suspend fun mongo(data: Container) {
    MongoClient.create("mongodb://localhost:27017").use { client ->
        val database = client.getDatabase("mydb")
        val collection = database.getCollection<Container>("containers")

        collection.insertOne(data)
        val doc = collection.find().first()

        println(doc)
    }
}
