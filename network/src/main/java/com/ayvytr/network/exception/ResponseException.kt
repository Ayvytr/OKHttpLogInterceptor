package com.ayvytr.network.exception

/**
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 2.3.0
 */
class ResponseException(message: String, val code: Int = 0, val messageStringId: Int? = -1,
                        cause: Throwable? = null):
    Exception(message, cause)