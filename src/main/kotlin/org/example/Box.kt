package org.example

import kotlinx.serialization.Serializable

@Serializable
data class Box<T>(
    val boxed: T,
)
