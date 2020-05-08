package com.ayvytr.coroutines

import kotlinx.coroutines.*

/**
 * @author admin
 */
fun main() = runBlocking {
    val activity = Activity()
    activity.doSomething()
    println("Launched coroutines")
    delay(500L)
    println("Destroying activity!")
    activity.destroy()
    delay(1000)
}

class Activity : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    fun destroy() {
        cancel()
    }

    fun doSomething() {
        repeat(10) { i ->
            launch {
                delay((i + 1) * 200L)
                println("Coroutine $i is done")
            }
        }
    }
}

