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
package com.ayvytr.network.cookie.cache

import com.ayvytr.network.cookie.SerializableCookie
import okhttp3.Cookie

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