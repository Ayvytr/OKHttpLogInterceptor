package com.ayvytr.network.bean

import com.ayvytr.network.exception.ResponseException

open class ResponseWrapper<T>(
    val data: T?,
    val page: Int = 1,
    val isLoadMore: Boolean = false,
    val hasMore: Boolean = false,
    val isSucceed: Boolean = true,
    val exception: ResponseException? = null
) {
    constructor(exception: ResponseException): this(null, isSucceed = false, exception = exception)

    constructor(message: String = "",
                code: Int = -1,
                cause: Throwable? = null,
                messageStringId: Int? = -1):
            this(exception = ResponseException(message, code, messageStringId, cause))


    val code by lazy { if (isSucceed) 200 else exception?.code ?: 200 }
    val message by lazy { exception?.message ?: "" }
    val messageStringId by lazy { exception?.messageStringId ?: -1 }
    val cause by lazy { exception?.cause }
    val isFailed by lazy { !isSucceed }
    val dataNonNull by lazy {
        data!!
    }

    override fun toString(): String {
        return "ResponseWrapper(data=$data, page=$page, isLoadMore=$isLoadMore, hasMore=$hasMore, " +
                "isSucceed=$isSucceed, exception=$exception)"
    }


}

