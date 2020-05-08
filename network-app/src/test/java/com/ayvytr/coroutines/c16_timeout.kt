package com.ayvytr.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

/**
 * @author admin
 */

fun main() = runBlocking {
    withTimeout(1300) {
        repeat(1000) { i ->
            println("I'm sleep $i ...")
            delay(500)
        }
    }
}