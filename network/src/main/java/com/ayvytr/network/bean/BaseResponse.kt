package com.ayvytr.network.bean

import com.ayvytr.network.exception.ResponseException


open class BaseResponse(
    val isSucceed: Boolean = true,
    val exception: ResponseException? = null
) {
    constructor(
        isSucceed: Boolean,
        message: String = "",
        code: Int = 0,
        throwable: Throwable? = null,
        messageStringId: Int? = -1
    ) : this(isSucceed, ResponseException(message, code, messageStringId, throwable))

    val code by lazy { exception?.code ?: 200 }
    val message by lazy { exception?.message ?: "" }
    val messageStringId by lazy { exception?.messageStringId ?: -1 }
    val cause by lazy { exception?.cause }
    val isFailed by lazy { !isSucceed }

    override fun toString(): String {
        return "BaseResponse(isSucceed=$isSucceed, exception=$exception)"
    }

}