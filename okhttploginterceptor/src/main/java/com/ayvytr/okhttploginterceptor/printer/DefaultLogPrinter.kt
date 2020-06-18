package com.ayvytr.okhttploginterceptor.printer

import android.util.Log
import com.ayvytr.okhttploginterceptor.Priority

/**
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 3.1.0
 */
class DefaultLogPrinter: IPrinter {
    override fun print(priority: Priority, tag: String, msg: String) {
        Log.println(priority.toInt(), tag, msg)
    }
}