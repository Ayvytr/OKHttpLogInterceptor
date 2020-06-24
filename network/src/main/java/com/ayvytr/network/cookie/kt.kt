package com.ayvytr.network.cookie

import okhttp3.Cookie

/**
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 2.3.0
 */
fun Cookie.toSerializeCookie(): SerializedCookie {
    return SerializedCookie(name, value, expiresAt, domain, path, secure, httpOnly, persistent,
                            hostOnly)
}