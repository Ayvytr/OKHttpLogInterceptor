package com.ayvytr.okhttploginterceptor

import junit.framework.Assert
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExtTest {
    @Test
    fun testMediaType() {
        val mediaType = "text/*".toMediaType()
        Assert.assertTrue(mediaType.isParsable())

        Assert.assertTrue(!"file/*".toMediaType().isParsable())

    }
}