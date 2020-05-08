package com.ayvytr.coroutines

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

/**
 * @author admin
 */

suspend fun doSomethingUsefulOne(): Int {
    delay(1000)
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000)
    return 29
}

fun main() = runBlocking {
//    val time = measureTimeMillis {
//        //不用async，俩方法是顺序执行的，所以最后时间是1秒+1秒+执行开销，大于1秒
//        val one = doSomethingUsefulOne()
//        val two = doSomethingUsefulTwo()
//        println("The answer is ${one + two}")
//    }
//    println("Completed in $time ms")
//
//    val timeAsync = measureTimeMillis {
//        //async 这里是并发执行的，时间是1秒过一点
//        val one = async { doSomethingUsefulOne() }
//        val two = async { doSomethingUsefulTwo() }
//        //async返回结果需要await接收
//        println("The answer is ${one.await() + two.await()}")
//    }
//
//    println("Completed in $timeAsync ms")

    val timeLazyAsync = measureTimeMillis {
        //lazy async
        val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne()}
        val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo()}
//        one.start()
//        two.start()
        println("The answer is ${one.await() + two.await()}")
    }
    println("Completed in $timeLazyAsync ms")
}
