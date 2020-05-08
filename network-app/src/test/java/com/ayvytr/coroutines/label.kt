package com.ayvytr.coroutines

/**
 * @author admin
 */

fun main() {
//    listOf(1, 2, 3, 4, 5, 6).forEach lit@{
//        if (it % 3 == 0) {
//            return@lit
//        }
//        print(it)
//    }
//    println(" done with explicit label")

    foo()
}

fun foo() {
    run loop@{
        listOf(1, 2, 3, 4, 5).forEach {
            if (it == 3) return@loop // 从传入 run 的 lambda 表达式非局部返回
            print(it)
        }
    }
    print(" done with nested loop")
}
