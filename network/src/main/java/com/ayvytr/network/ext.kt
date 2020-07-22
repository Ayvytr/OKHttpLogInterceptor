package com.ayvytr.network

import com.ayvytr.network.bean.ResponseWrapper
import com.ayvytr.network.exception.ResponseException
import okhttp3.Cookie

/**
 * @author Administrator
 */
fun <T> Throwable.toResponseException(): ResponseException {
    return ApiClient.parseException.invoke(this)
}

fun <T> Throwable.wrap(): ResponseWrapper<T> {
    return ResponseWrapper(null, isSucceed = false, exception = this.toResponseException<T>())
}

fun <T> T.wrap(
    isSucceed: Boolean = true,
    page: Int = 1,
    isLoadMore: Boolean = false,
    hasMore: Boolean = false,
    exception: Throwable? = null
): ResponseWrapper<T> {
    return ResponseWrapper(this, page, isLoadMore, hasMore, isSucceed,
                           exception = exception?.toResponseException<T>())
}

fun Cookie.isExpired(): Boolean {
    return expiresAt < System.currentTimeMillis()
}