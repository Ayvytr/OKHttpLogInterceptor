package com.ayvytr.network.bean

open class ResponseWrapper<T>(
    val data: T?,
    val page: Int = 1,
    val isLoadMore: Boolean = false,
    val hasMore: Boolean = false,
    isSucceed: Boolean = true,
    exception: Throwable? = null,
    message:String="",
    code: Int = -1,
    messageStringId: Int? = -1
): BaseResponse(isSucceed, exception, message, code, messageStringId) {

    val dataNonNull by lazy {
        data!!
    }

}

