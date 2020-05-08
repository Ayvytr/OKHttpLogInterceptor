package com.ayvytr.coroutines

/**
 * @author admin
 */

fun main() {
    val value = calculate()
    println(value)
}

fun calculate(): Int{
    val i = 33
    return when(i) {
        is Number -> 1
        else -> 0
    }
}
