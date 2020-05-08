package com.ayvytr.coroutines

/**
 * @author admin
 */

fun main(){
    var str = "string"
    str = str.apply {
        this.reversed()
    }
    println(str)
    str = str.also {
        it.reversed()
    }
    println(str)

    str = str.run {
        this.reversed()
    }
    println(str)

    str = with(str) {
        str.reversed()
    }
    println(str)

    str = str.let {
        it.reversed()
    }
    println(str)
}