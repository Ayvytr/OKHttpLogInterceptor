package com.ayvytr.okhttploginterceptor.printer

import com.ayvytr.okhttploginterceptor.Priority

/**
 * printer接口，可自定义log输出逻辑.
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 3.1.0
 */

interface IPrinter {
    fun print(priority: Priority, tag: String, msg: String)
}