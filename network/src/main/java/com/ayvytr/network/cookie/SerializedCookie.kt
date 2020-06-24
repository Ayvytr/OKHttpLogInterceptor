package com.ayvytr.network.cookie

import okhttp3.Cookie
import java.io.Serializable

/**
 * [Cookie]序列化的复制类，只作为读写Object的工具类.
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 2.3.0
 */
data class SerializedCookie(val name: String,
                            val value: String,
                            val expiresAt: Long,
                            val domain: String,
                            val path: String,
                            val secure: Boolean,
                            val httpOnly: Boolean,
                            val persistent: Boolean,
                            val hostOnly: Boolean)

    : Serializable {

    fun toCookie(): Cookie {
        val builder = Cookie.Builder()
            .name(name)
            .value(value)
            .domain(domain)
            .path(path)

        builder.expiresAt(expiresAt)

        if (secure) {
            builder.secure()
        }
        if (httpOnly) {
            builder.httpOnly()
        }
        if (hostOnly) {
            builder.hostOnlyDomain(domain)
        }

        return builder.build()
    }


}