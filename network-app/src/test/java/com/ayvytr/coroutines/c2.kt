package com.ayvytr.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong

/**
 * @author admin
 */
fun main() {
    //thread
//    val c = AtomicLong()
//    for (i in 1..1_000_000L) {
//        thread(start = true) {
//            c.addAndGet(i)
//            println(i)
//        }
//    }
//
//    println(c.get())

    //协程coroutines, 但是不1秒就完成了，打印了任意数字，一些协程没有在main打印结果之前执行完毕了
    val c = AtomicLong()
    for (i in 1..1_000_000L) {
        GlobalScope.launch {
            c.addAndGet(i)
            println(i)
        }
    }
    println(c.get())
}