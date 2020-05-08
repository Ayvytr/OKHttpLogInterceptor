package com.ayvytr.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * @author admin
 */

fun main() = runBlocking {
    GlobalScope.launch {
        repeat(1000) {
            println("I'm sleeping $it")
            delay(500)
        }
    }

    delay(1300)
}