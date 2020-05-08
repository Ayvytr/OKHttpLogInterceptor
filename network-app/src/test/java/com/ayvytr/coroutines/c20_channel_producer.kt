package com.ayvytr.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking

/**
 * @author admin
 */

fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce {
    for (x in 1..5) {
        send(x * x)
    }
}

fun main() = runBlocking {
    val squares = produceSquares()
    squares.consumeEach(::println)
    println("Done!")
}
