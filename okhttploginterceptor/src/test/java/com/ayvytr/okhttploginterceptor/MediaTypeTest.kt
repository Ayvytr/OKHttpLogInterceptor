package com.ayvytr.okhttploginterceptor

import junit.framework.TestCase.assertTrue
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Test

/**
 * @author Do
 */
class MediaTypeTest {
    @Test
    fun testMediaType() {
        var mediaType = "text/*".toMediaType()
        assertTrue(mediaType.isParsable())
        mediaType = "text/html".toMediaType()
        assertTrue(mediaType.isParsable())
        mediaType = "application/json".toMediaType()
        assertTrue(mediaType.isParsable())

        mediaType = "video/*".toMediaType()
        assertTrue(!mediaType.isParsable())

        mediaType = "audio/*".toMediaType()
        assertTrue(!mediaType.isParsable())

        mediaType = "FILE/*".toMediaType()
        assertTrue(!mediaType.isParsable())

    }

    @Test
    fun isJson() {
        val s = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Alice\",\n" +
                "    \"hobbies\": [\"reading\", \"hiking\"]\n" +
                "}"
        val s2 = "[\n" +
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
        println(s)
        println(s2)
//        assertTrue(s.isGuessJson())
        assertTrue(s2.isGuessJson())
    }
}