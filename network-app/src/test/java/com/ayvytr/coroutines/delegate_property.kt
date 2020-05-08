package com.ayvytr.coroutines

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * @author admin
 */

class Example {
    var p: String by Delegate()
}

class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me!\n"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, s: String) {
        println("$s has been assigned to '${property.name}' in $thisRef")
    }

}

class User {
    var name: String by Delegates.observable("<no name>") { prop, old, new ->
        println("$old -> $new")
    }

    var intercept: String by Delegates.vetoable("<null intercept>") { property, oldValue, newValue ->
        if (newValue == "new") {
            return@vetoable true
        }

        return@vetoable false
    }
}

fun main() {
//    val e = Example()
//    e.p = "NEW"
//    println(e.p)

    val user = User()
    user.name = "first"
    user.name = "second"

    user.intercept = "intercept"
    println(user.intercept)
    user.intercept = "new"
    println(user.intercept)
}
