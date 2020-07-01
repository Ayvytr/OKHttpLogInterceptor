package com.ayvytr.network.cookie.cache

import okhttp3.Cookie

/**
 * A CookieCache handles the volatile cookie session storage.
 */
interface CookieCache : Iterable<Cookie> {
    /**
     * Add all the new cookies to the session, existing cookies will be overwritten.
     *
     * @param cookies
     */
    fun addAll(cookies: Collection<Cookie>)

    /**
     * Clear all the cookies from the session.
     */
    fun clear()
}