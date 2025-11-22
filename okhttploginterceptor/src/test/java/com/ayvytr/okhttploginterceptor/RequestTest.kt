package com.ayvytr.okhttploginterceptor

import junit.framework.Assert.assertTrue
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * @author Do
 */
class RequestTest {
    lateinit var request: Request
    val file = File("test.txt")

    @Before
    fun init() {
        if (!file.exists()) {
            file.createNewFile()
            file.writeText("11111")
        }
        val requestBody = RequestBody.create(
            "file/*".toMediaType(),
            file)
        println(requestBody.contentType())
        request = Request.Builder()
            .url("http://test.com")
            .post(requestBody)
            .header("key", "header")
            .build()
    }

    @Test
    fun test1() {
        println(request.body!!.contentType()!!.isParsable())
        assertTrue(!(request.body?.contentType()?.isParsable() ?: false))
    }

    @After
    fun clear() {
        if (file.exists()) {
            file.delete()
        }
    }
}