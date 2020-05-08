package com.ayvytr.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

/**
 * @author admin
 */

fun somethingUsefulOneAsync() = GlobalScope.async {
    doSomethingUsefulOne()
}

fun somethingUsefulTwoAsync() = GlobalScope.async {
    doSomethingUsefulTwo()
}

fun main() {
    val time = measureTimeMillis {
        //异步执行协程
        val one = somethingUsefulOneAsync()
        val two = somethingUsefulTwoAsync()

        //等待结果必须调用其他的挂起或者阻塞，可使用runBlocking阻塞主线程
        runBlocking {
            println("The answer is ${one.await() + two.await()}")
        }
    }

    println("Completed in $time ms")
}
