package com.ayvytr.okhttploginterceptor

import junit.framework.Assert.assertTrue
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * @author Do
 */
class RequestTest {
    lateinit var request: Request

    @Before
    fun init() {
        request = Request.Builder()
            .url("http://test.com")
            .post(RequestBody.create("file/1.png".toMediaType(),
                                     File("C:\\Users\\Do\\Desktop\\android.png")))
            .header("key", "header")
            .build()
    }

    @Test
    fun test1() {
        assertTrue(request.body?.contentType()?.isUnreadable() ?: false)
    }
}