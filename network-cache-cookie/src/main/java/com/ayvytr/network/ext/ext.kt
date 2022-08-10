package com.ayvytr.network.ext

import okhttp3.Cookie

fun Cookie.isExpired(): Boolean {
    return expiresAt < System.currentTimeMillis()
}