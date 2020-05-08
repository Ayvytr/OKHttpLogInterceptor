package com.ayvytr.coroutines

/**
 * @author admin
 */

fun main() {
    printAllValues<RGB>()
}

enum class RGB {
    RED, GREEN, BLUE
}

inline fun <reified T : Enum<T>> printAllValues() {
    print(enumValues<T>().joinToString { it.name })
}
