package com.ayvytr.coroutines

import kotlinx.coroutines.*

/**
 * @author admin
 */

fun main() = runBlocking{
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }

    val job = GlobalScope.launch(handler) {
        throw AssertionError()
    }
    val deferred = GlobalScope.async(handler) {
        throw ArithmeticException()
    }
    joinAll(job, deferred)
}