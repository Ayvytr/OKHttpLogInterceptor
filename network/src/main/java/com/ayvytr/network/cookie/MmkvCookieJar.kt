/*
 * Copyright (C) 2016 Francisco José Montiel Navarro.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ayvytr.network.cookie

import com.ayvytr.network.cookie.cache.SetCookieCache
import com.ayvytr.network.cookie.saver.MmkvCookieSaver
import com.ayvytr.network.isExpired
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.util.*

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