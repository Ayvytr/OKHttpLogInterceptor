package com.ayvytr.coroutines

/**
 * @author admin
 */

fun main() {
//    Person()

//    Man("man")
    /**
     * 输出结果：
     * null and end
    woman's name
    woman's name
    基类构造函数要避免使用open属性，防止发生意外结果
     */
//    Woman("woman")

    C().f()
}


open class Person constructor(open var firstName: String) {
    constructor() : this("default name") {
    }

    init {
        firstName += " and end"
        println(firstName)
    }
}

class Man(name: String) : Person(name) {
    override var firstName = "man's name"

    init {
        println(firstName)
    }
}

class Woman : Person {
    override var firstName = "woman's name"

    constructor(name: String) : super(name) {
        println(firstName)
    }

    init {
        println(firstName)
    }
}


open class A {
    open fun f() {
        println("A")
    }

    fun a() {
        println("a")
    }
}

interface B {
    fun f() {
        println("B")
    }

    fun b() {
        println("b")
    }
}

class C(): A(), B {
    override fun f() {
        super<A>.f()
        super<B>.f()
    }
}