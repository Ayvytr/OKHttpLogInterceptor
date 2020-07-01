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
package com.ayvytr.network.cookie.saver

import com.ayvytr.network.cookie.SerializableCookie
import com.ayvytr.network.provider.ContextProvider
import com.tencent.mmkv.MMKV
import okhttp3.Cookie
import java.util.*

class MmkvCookieSaver : CookieSaver {
    private val mmkv by lazy {
        MMKV.initialize(ContextProvider.globalContext)
        MMKV.mmkvWithID("okhttp-cookiejar")
    }

    override fun loadAll(): List<Cookie> {
        if (mmkv.count() <= 0) {
            return emptyList()
        }
        val keys = mmkv.allKeys()
        val cookies: MutableList<Cookie> = ArrayList(keys.size)
        for (key in keys) {
            val bytes: ByteArray? = mmkv.decodeBytes(key)
            if (bytes == null || bytes.isEmpty()) {
                continue
            }
            val cookie = SerializableCookie.decode(bytes)
            if (cookie != null) {
                cookies.add(cookie)
            }
        }
        return cookies
    }

    override fun saveAll(cookies: Collection<Cookie>) {
        for (cookie in cookies) {
            mmkv.encode(
                createCookieKey(cookie),
                SerializableCookie(cookie).encode()
            )
        }
    }

    override fun removeAll(cookies: Collection<Cookie>) {
        for (cookie in cookies) {
            mmkv.remove(createCookieKey(cookie))
        }
    }

    override fun clear() {
        mmkv.clearAll()
    }

    companion object {
        private fun createCookieKey(cookie: Cookie): String {
            return (if (cookie.secure) "https" else "http") + "://" + cookie.domain + cookie.path + "|" + cookie.name
        }
    }

}