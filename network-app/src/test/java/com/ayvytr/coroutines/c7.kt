package com.ayvytr.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * @author admin
 */

fun main() = runBlocking {
    launch {
        delay(1000)
        println("World!")
    }
    print("Hello, ")
}