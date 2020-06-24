package com.ayvytr.network.cookie

import com.ayvytr.network.ApiClient
import com.tencent.mmkv.MMKV
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.io.*

/**
 * 借助[MMKV]实现存储的[CookieJar].
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 2.3.0
 */
class MmkvCookieJar: CookieJar {

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        if (!mmkv.containsKey(url.toString())) {
            return emptyList()
        }

        val decodeBytes = mmkv.decodeBytes(url.toString())
        val bis = ByteArrayInputStream(decodeBytes)
        val ois = ObjectInputStream(bis)
        val list = ois.readObject() as List<SerializedCookie>
        ois.close()
        bis.close()
        return toCookies(list)
    }

    private fun toCookies(serializedCookies: List<SerializedCookie>): List<Cookie> {
        val list = mutableListOf<Cookie>()
        serializedCookies.forEach {
            list.add(it.toCookie())
        }
        return list
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(toSerializeCookies(cookies))
        val byteArray = byteArrayOutputStream.toByteArray()
        mmkv.encode(url.toString(), byteArray)

        objectOutputStream.close()
        byteArrayOutputStream.close()
    }

    private fun toSerializeCookies(cookies: List<Cookie>): List<SerializedCookie> {
        val list= mutableListOf<SerializedCookie>()
        cookies.forEach {
            list.add(it.toSerializeCookie())
        }
        return list
    }

    fun clear() {
        mmkv.clearAll()
    }

    companion object {
        val CACHE_ROOT_DIR by lazy {
            File(ApiClient.DEFAULT_CACHE_DIR, "OkHttp-Cookies").absolutePath
        }

        val mmkv by lazy {
            MMKV.initialize(CACHE_ROOT_DIR)
            MMKV.defaultMMKV()
        }
    }
}