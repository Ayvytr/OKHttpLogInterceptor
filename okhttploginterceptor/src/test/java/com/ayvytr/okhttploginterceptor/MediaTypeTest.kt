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
        assertTrue(!mediaType.isUnreadable())
        mediaType = "text/html".toMediaType()
        assertTrue(!mediaType.isUnreadable())
        mediaType = "application/json".toMediaType()
        assertTrue(!mediaType.isUnreadable())

        mediaType = "video/*".toMediaType()
        assertTrue(mediaType.isUnreadable())

        mediaType = "audio/*".toMediaType()
        assertTrue(mediaType.isUnreadable())

        mediaType = "FILE/*".toMediaType()
        assertTrue(mediaType.isUnreadable())

    }
}