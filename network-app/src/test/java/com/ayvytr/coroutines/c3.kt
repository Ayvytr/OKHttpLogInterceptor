package com.ayvytr.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/**
 * 从协程返回一个值
 * @author admin
 */

fun main() {
    val deferred = (1..1_000_000).map { n ->
        GlobalScope.async {
            delay(1000)
            n
        }
    }

    runBlocking {
        val sum = deferred.sumBy { it.await() }
        println("sum: ${sum}")
    }
}

suspend fun worlload(n:Int):Int {
    delay(1000)
    return n
}
