package com.ayvytr.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * @author admin
 */

fun main() = runBlocking {
    val request = launch {
        GlobalScope.launch {
            println("job1: I'm run in GlobalScope and execute independently!")
            delay(1000)
            println("job1: I'm not affected by cancellation of the request")
        }
    }

    launch {
        delay(100)
        println("job2: i'm a child of the request coroutine")
        delay(1000)
        println("job2: I will not execute this line if my parent request is cancelled")
    }

    delay(500)
    request.cancel()
    delay(1000)
    println("main: Who has survived request cancellation?")
}