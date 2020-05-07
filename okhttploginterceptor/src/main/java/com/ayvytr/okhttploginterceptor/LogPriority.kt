package com.ayvytr.okhttploginterceptor

/**
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 5.0.0
 */

enum class LogPriority(private val priority: Int){

    V(2),
    D(3),
    I(4),
    W(5),
    E(6),
    A(7);

    fun toInt(): Int {
        return priority
    }
}