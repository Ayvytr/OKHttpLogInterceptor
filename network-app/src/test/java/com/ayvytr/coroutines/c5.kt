package com.ayvytr.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * @author admin
 */

fun main() = runBlocking {
    //启动新协程
    GlobalScope.launch {
        delay(1000)
        println("World!")
    }

    println("Hello, ")
    delay(2000)
}


