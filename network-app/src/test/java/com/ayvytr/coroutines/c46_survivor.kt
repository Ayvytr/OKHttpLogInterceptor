package com.ayvytr.coroutines

import kotlinx.coroutines.*

/**
 * @author admin
 */

fun main() = runBlocking {
    val supervisor = SupervisorJob()
    with(CoroutineScope(coroutineContext + supervisor)) {
        val firstChild = launch(CoroutineExceptionHandler { _, _ -> }) {
            println("First child is failing")
            throw AssertionError("First child is cancelled")
        }
        val secondChild = launch {
            firstChild.join()
            println("First child is cancelled: ${firstChild.isCancelled}, but second one is still alive")
            try {
                delay(Long.MAX_VALUE)
            } finally {
                println("Second child is cancelled because supervisor is cancelled")
            }
        }

        firstChild.join()
        println("Cancelling supervisor")
        supervisor.cancel()
        secondChild.join()
    }
}