package com.ayvytr.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * @author admin
 */

fun main() {
    println("Start")
    GlobalScope.launch {
        delay(1000)
        println("hello")
    }
//    Thread.sleep(2000)
    runBlocking {
        delay(2000)
    }
    println("Stop")
}


