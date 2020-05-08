package com.ayvytr.coroutines

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/**
 * @author admin
 */

fun main() = runBlocking(CoroutineName("main")) {
    log("Started main coroutine")
    val v1 = async(CoroutineName("v1coroutine")) {
        delay(500)
        log("Computing v1")
        252
    }

    val v2 = async(CoroutineName("v2coroutine")) {
        delay(1000)
        log("Computing v2")
        6
    }

    log("The answer for v1 / v2 = ${v1.await() / v2.await()}")
}