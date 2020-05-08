package com.ayvytr.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * @author admin
 */

val mutex = Mutex()
var c50 = 0

fun main() = runBlocking {
    withContext(Dispatchers.Default) {
        massiveRun {
            mutex.withLock {
                c50++
            }
        }
    }
    println("Counter = $c50")
}


