package com.ayvytr.network.ext.cookie.saver

import okhttp3.Cookie

/**
 * A CookiePersistor handles the persistent cookie storage.
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 3.0.0
 */
interface CookieSaver {
    fun loadAll(): List<Cookie>

    /**
     * Persist all cookies, existing cookies will be overwritten.
     *
     * @param cookies cookies persist
     */
    fun saveAll(cookies: Collection<Cookie>)

    /**
     * Removes indicated cookies from persistence.
     *
     * @param cookies cookies to remove from persistence
     */
    fun removeAll(cookies: Collection<Cookie>)

    /**
     * Clear all cookies from persistence.
     */
    fun clear()
}