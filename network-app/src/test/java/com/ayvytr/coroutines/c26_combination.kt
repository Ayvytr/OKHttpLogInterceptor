package com.ayvytr.coroutines

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

/**
 * @author admin
 */

fun main() = runBlocking {
//    val time = measureTimeMillis {
//
//        val one = doSomethingUsefulOne()
//        val two = doSomethingUsefulTwo()
//        println("The answer is ${one + two}")
//    }
    val time = measureTimeMillis {
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        println("The answer is ${one.await() + two.await()}")
    }

    println("Completed in $time ms")
}