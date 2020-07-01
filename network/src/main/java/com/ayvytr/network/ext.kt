package com.ayvytr.network

import com.ayvytr.network.bean.ResponseWrapper
import okhttp3.Cookie

/**
 * @author Administrator
 */
fun <T> Throwable.wrap(): ResponseWrapper<T> {
    return ResponseWrapper(null, isSucceed = false, exception = this)
}

fun <T> T.wrap(
    isSucceed: Boolean = true,
    page: Int = 1,
    isLoadMore: Boolean = false,
    hasMore: Boolean = false,
    exception: Exception? = null
): ResponseWrapper<T> {
    return ResponseWrapper(this, page, isLoadMore, hasMore, isSucceed, exception = exception)
}

fun Cookie.isExpired(): Boolean {
    return expiresAt < System.currentTimeMillis()
}