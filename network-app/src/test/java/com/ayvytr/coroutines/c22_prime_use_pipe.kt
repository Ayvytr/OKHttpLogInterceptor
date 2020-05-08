package com.ayvytr.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking

/**
 * @author admin
 */

fun CoroutineScope.numbersFrom(start: Int) = produce {
    var x = start
    while (true)
        send(x++)
}

fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int) = produce {
    for (x in numbers) {
        if (x % prime != 0) {
            send(x)
        }
    }
}

fun main() = runBlocking {
    var cur = numbersFrom(2)
    for (i in 1..10) {
        val prime = cur.receive()
        println(prime)
        cur = filter(cur, prime)
    }
    coroutineContext.cancelChildren()
}
