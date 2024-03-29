package com.ayvytr.network.ext.interceptor

import com.ayvytr.network.ext.isNetworkAvailable
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 3.0.0
 */
internal class CacheNetworkInterceptor(val maxAgeSeconds: Int = 3600) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val cacheHeaderValue = if (isNetworkAvailable()) "public, max-age=$maxAgeSeconds"
                                else "public, only-if-cached, max-stale=$maxAgeSeconds"
        val request: Request = originalRequest.newBuilder().build()
        val response: Response = chain.proceed(request)
        return response.newBuilder()
            .removeHeader("Pragma")
            .removeHeader("Cache-Control")
            .header("Cache-Control", cacheHeaderValue)
            .build()
    }
}

