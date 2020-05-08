package com.ayvytr.coroutines

import kotlinx.coroutines.*

/**
 * @author admin
 */

fun main() = runBlocking {
    val job = launch {
        try {
            repeat(1000) { i ->
                println("job: I'm sleeping $i ...")
                delay(500)
            }
        } finally {
            withContext(NonCancellable) {
                println("job: i'm running finally")
                delay(1000)
                println("job: And I've just delayed ofr 1 sec because I'm non-cancellable")
            }
        }
    }

    delay(1300)
    println("main: I'm tired of waiting!")
    job.cancelAndJoin()
    println("main: Now I can quit.")
}