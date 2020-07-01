package com.ayvytr.network.bean

open class ResponseWrapper<T> constructor(
    val data: T?,
    val page: Int = 1,
    val isLoadMore: Boolean = false,
    val hasMore: Boolean = false,
    isSucceed: Boolean = true,
    message: String = "",
    code: Int = -1,
    exception: Throwable? = null,
    messageStringId: Int? = -1
): BaseResponse(isSucceed, message, code, exception, messageStringId) {

    val dataNonNull by lazy {
        data!!
    }

    override fun toString(): String {
        return "ResponseWrapper(data=$data, page=$page, isLoadMore=$isLoadMore, hasMore=$hasMore, " +
                "isSucceed=$isSucceed, $message, $code, $exception, $messageStringId)"
    }
}

