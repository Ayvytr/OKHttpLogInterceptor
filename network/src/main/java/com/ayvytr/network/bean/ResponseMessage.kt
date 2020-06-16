package com.ayvytr.network.bean


class ResponseMessage(
    val message: String? = null,
    val messageStringId: Int? = -1,
    val code: Int = 0,
    val throwable: Throwable? = null
)