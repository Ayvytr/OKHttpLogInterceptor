package com.ayvytr.coroutines

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * @author admin
 */

fun main() = runBlocking {
    val channel = Channel<Int>()
    launch {
        for (x in 1..5) {
            channel.send(x * x)
        }
    }

    repeat(5) {
        println(channel.receive())
    }

    println("Done!")
}