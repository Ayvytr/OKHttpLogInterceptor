package com.ayvytr.coroutines

import kotlinx.coroutines.runBlocking

/**
 * @author admin
 */

fun main() = runBlocking {
    repeat(1000000) {
        //        launch {
//            delay(1000)
//            print(".")
//        }
        Thread(Runnable {
            Thread.sleep(1000)
            print(".")
        }).start()
    }
}