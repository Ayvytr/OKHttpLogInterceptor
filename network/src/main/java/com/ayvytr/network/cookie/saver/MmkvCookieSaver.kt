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