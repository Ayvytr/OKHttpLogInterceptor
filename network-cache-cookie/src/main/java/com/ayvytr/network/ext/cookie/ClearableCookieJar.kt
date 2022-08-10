package com.ayvytr.network.ext.cookie

import okhttp3.CookieJar

/**
 * This interface extends [CookieJar] and adds methods to clear the cookies.
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 3.0.0
 */
interface ClearableCookieJar : CookieJar {
    /**
     * Clear all the session cookies while maintaining the persisted ones.
     */
    fun clearSession()

    /**
     * Clear all the cookies from persistence and from the cache.
     */
    fun clear()
}