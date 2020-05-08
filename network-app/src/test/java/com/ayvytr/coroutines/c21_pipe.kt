package com.ayvytr.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking

/**
 * @author admin
 */

fun main() = runBlocking {
    val numbers = produceNumbers()
    val squares = square(numbers)
    for (i in 1..10) {
        println(squares.receive())
    }

    println("Done!")
    //取消子协程
    coroutineContext.cancelChildren()
}

fun CoroutineScope.produceNumbers() = produce<Int> {
    var x = 1
    while (true) {
        send(x++)
    }
}

fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
    for (x in numbers) {
        send(x * x)
    }
}
