package com.ayvytr.coroutines

import kotlinx.coroutines.*

/**
 * @author admin
 */
fun main() = runBlocking {

    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }


    val job = GlobalScope.launch(handler) {
        launch {
            try {
                delay(Long.MAX_VALUE)
            } finally {
                withContext(NonCancellable) {
                    println("Children are cancelled, but exception is no handled until all children terminate")
                    delay(100)
                    println("The first child finished its non cancellable block")
                }
            }
        }
        launch {
            delay(10)
            println("Second child throws an exception")
            throw ArithmeticException()
        }
    }
    job.join()
}
