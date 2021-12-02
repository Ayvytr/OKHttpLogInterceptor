package com.ayvytr.network

import android.support.test.runner.AndroidJUnit4
import com.ayvytr.network.TestCookieCreator.createPersistentCookie
import com.ayvytr.network.cookie.MmkvCookieJar
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Administrator
 */

@RunWith(AndroidJUnit4::class)
class CookieJarTest {
    val url = "https://domain.com/".toHttpUrlOrNull()!!

    lateinit var mmkvCookieJar: MmkvCookieJar

    @Before
    fun before() {
        mmkvCookieJar = MmkvCookieJar()
        mmkvCookieJar.clear()
    }

    @Test
    fun testPersistent() {
        var cookie = createPersistentCookie(false)
        mmkvCookieJar.saveFromResponse(url, listOf(cookie))
        var savedList = mmkvCookieJar.loadForRequest(url)
        Assert.assertEquals(cookie, savedList[0])

        cookie = createPersistentCookie(true)
        mmkvCookieJar.saveFromResponse(url, listOf(cookie))
        savedList = mmkvCookieJar.loadForRequest(url)
        Assert.assertEquals(cookie, savedList[0])
    }

    @Test
    fun testExpired() {
        val cookie =
            TestCookieCreator.createExpiredCookie()
        mmkvCookieJar.saveFromResponse(url, listOf(cookie))

        val cookies: List<Cookie> = mmkvCookieJar.loadForRequest(url)
        Assert.assertEquals(cookie, cookies[0])
    }

    @Test
    fun testUpdate() {
        mmkvCookieJar.saveFromResponse(url, listOf(createPersistentCookie("name", "first")))

        val newCookie = createPersistentCookie("name", "last")
        mmkvCookieJar.saveFromResponse(url, listOf(newCookie))

        val storedCookies: List<Cookie> = mmkvCookieJar.loadForRequest(url)
        Assert.assertTrue(storedCookies.size == 1)
        Assert.assertEquals(newCookie, storedCookies[0])
    }

    @Test
    fun testClear() {
        mmkvCookieJar.clear()
        val savedList = mmkvCookieJar.loadForRequest(url)
        Assert.assertTrue(savedList.isEmpty())
    }
}