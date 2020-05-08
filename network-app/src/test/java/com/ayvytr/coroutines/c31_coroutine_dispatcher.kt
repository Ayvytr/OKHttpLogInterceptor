package com.ayvytr.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking

/**
 * @author admin
 */

fun main() = runBlocking<Unit> {
    launch { println("main runBlocking: I'm working in thread ${Thread.currentThread().name}") }

    launch(Dispatchers.Unconfined) {
        println("Unconfined: I'm working in thread ${Thread.currentThread().name}")
    }

    launch(Dispatchers.Default) {
        println("Default: I'm working in thread ${Thread.currentThread().name}")
    }

    launch(newSingleThreadContext("MyOwnThread")) {
        println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
    }
}