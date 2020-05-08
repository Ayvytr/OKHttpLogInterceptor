package com.ayvytr.coroutines

/**
 * @author admin
 */

interface Base {
    fun printMessage()
    fun printMessageLine()
    val message: String
    fun print()
}

class BaseImpl(val x: Int) : Base {
    override fun printMessage() {
        print(x)
    }

    override fun printMessageLine() {
        println(x)
    }

    override val message = "BaseImpl: x = $x"
    override fun print() {
        println(message)
    }
}

class Derived(b: Base) : Base by b {
    override fun printMessage() {
        print("abc")
    }

    // 在 b 的 `print` 实现中不会访问到这个属性
    override val message = "Message of Derived"
}

fun main() {
    val b = BaseImpl(10)
    Derived(b).printMessage()
    println()
    Derived(b).printMessageLine()

    println()

    val derived = Derived(b)
    derived.print()
    println(derived.message)
}