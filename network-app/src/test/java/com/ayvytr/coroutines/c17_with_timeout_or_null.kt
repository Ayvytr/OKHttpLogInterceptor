package com.ayvytr.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

/**
 * @author admin
 */

fun main() = runBlocking {
    val result = withTimeoutOrNull(1300) {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500)
        }
        "Done"
    }

    println("Result is $result")
}