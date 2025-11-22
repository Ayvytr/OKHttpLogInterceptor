package com.ayvytr.okhttploginterceptor

import junit.framework.TestCase.assertTrue
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * @author Do
 */
class ResponseTest {
    lateinit var s: String
    lateinit var s2: String

    @Before
    fun init() {
        s = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Alice\",\n" +
                "    \"hobbies\": [\"reading\", \"hiking\"]\n" +
                "}"
        s2 = "[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Alice\",\n" +
                "    \"hobbies\": [\"reading\", \"hiking\"]\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"Bob\",\n" +
                "    \"hobbies\": [\"gaming\"]\n" +
                "  }\n" +
                "]"

    }

    @Test
    fun test1() {
        var responseBody = s.toResponseBody()
        println(responseBody.contentType())
        assertTrue(responseBody.string().isGuessJson())

        responseBody = s.toResponseBody("application/json".toMediaType())
        println(responseBody.contentType())
        assertTrue(responseBody.string().isGuessJson())

        val responseBody2 = s.toResponseBody()
        println(responseBody2.contentType())
        assertTrue(responseBody2.string().isGuessJson())

    }

    @After
    fun clear() {
    }
}