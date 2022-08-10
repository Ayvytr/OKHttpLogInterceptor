package com.ayvytr.network.ext.cookie

import com.ayvytr.network.ext.cookie.cache.SetCookieCache
import com.ayvytr.network.ext.cookie.saver.MmkvCookieSaver
import com.ayvytr.network.ext.isExpired
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.util.*

/**
 * [com.tencent.mmkv.MMKV]实现的[okhttp3.CookieJar].
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 3.0.0 拆分到扩展包
 * @since 2.3.0
 */
class MmkvCookieJar : ClearableCookieJar {
    private val cache = SetCookieCache()
    private val persistor = MmkvCookieSaver()

    init {
        this.cache.addAll(persistor.loadAll())
    }

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cache.addAll(cookies)
        persistor.saveAll(filterPersistentCookies(cookies))
    }

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookiesToRemove: MutableList<Cookie> =
            ArrayList()
        val validCookies: MutableList<Cookie> = ArrayList()
        val it = cache.iterator() as SetCookieCache.SetCookieCacheIterator
        while (it.hasNext()) {
            val currentCookie = it.next()
            if (currentCookie.isExpired()) {
                cookiesToRemove.add(currentCookie)
                it.remove()
            } else if (currentCookie.matches(url)) {
                validCookies.add(currentCookie)
            }
        }
        persistor.removeAll(cookiesToRemove)
        return validCookies
    }

    @Synchronized
    override fun clearSession() {
        cache.clear()
        cache.addAll(persistor.loadAll())
    }

    @Synchronized
    override fun clear() {
        cache.clear()
        persistor.clear()
    }

    companion object {
        private fun filterPersistentCookies(cookies: List<Cookie>): List<Cookie> {
            val persistentCookies: MutableList<Cookie> =
                ArrayList()
            for (cookie in cookies) {
                if (cookie.persistent) {
                    persistentCookies.add(cookie)
                }
            }
            return persistentCookies
        }

    }
}