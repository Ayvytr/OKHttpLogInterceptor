package com.ayvytr.coroutines

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * @author admin
 */

fun main() = runBlocking{
    val job = launch {
        repeat(1000) { i ->
            println("job: I'm sleeping $i ...")
            delay(500)
        }
    }

    delay(1300)
    println("main: I'm tired of waiting!")
//    job.cancel()
//    job.join()
    job.cancelAndJoin()
    println("main: Now I can quit.")
}