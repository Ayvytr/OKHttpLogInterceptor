package com.ayvytr.network.ext.cookie.cache

import com.ayvytr.network.ext.cookie.SerializableCookie
import okhttp3.Cookie

/**
* @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
* @since 3.0.0
*/
class SetCookieCache : CookieCache {
    private val cookies by lazy { hashSetOf<SerializableCookie>() }

    override fun addAll(newCookies: Collection<Cookie>) {
        cookies.addAll(SerializableCookie.decorateAll(newCookies))
    }

    override fun clear() {
        cookies.clear()
    }

    override fun iterator(): Iterator<Cookie> {
        return SetCookieCacheIterator()
    }

    inner class SetCookieCacheIterator :
        MutableIterator<Cookie> {
        private val iterator = cookies.iterator()
        override fun hasNext(): Boolean {
            return iterator.hasNext()
        }

        override fun next(): Cookie {
            return iterator.next().cookie
        }

        override fun remove() {
            iterator.remove()
        }
    }

}