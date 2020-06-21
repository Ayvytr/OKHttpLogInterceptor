package com.ayvytr.network.exception

/**
 * @author Do
 */

class ResponseException(message: String, val code: Int = 0, val messageStringId: Int? = -1,
                        cause: Throwable? = null):
    Exception(message, cause)