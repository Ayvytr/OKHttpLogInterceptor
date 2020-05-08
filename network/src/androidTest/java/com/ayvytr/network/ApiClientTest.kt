package com.ayvytr.network

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.ayvytr.commonlibrary.bean.BaseGank
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Call
import retrofit2.http.GET

/**
 * @author admin
 */

@RunWith(AndroidJUnit4::class)
class ApiClientTest {
    interface ApiService {
        @GET("data/iOS/2/1")
        fun getIosGank(): Call<BaseGank>

        @GET("data/Android/2/1")
        fun getAndroidGank(): Call<BaseGank>
    }

    interface As2 {

    }

    @Test
    fun testInit() {
        val appContext = InstrumentationRegistry.getTargetContext()

        val apiClient = ApiClient.getInstance()
        apiClient.init("http://google.com")

        Assert.assertNotNull(apiClient)
        Assert.assertEquals(apiClient.baseUrl, "http://google.com")
        Assert.assertEquals(apiClient.getRetrofit(), apiClient.getRetrofit())

        val apiService = apiClient.create(ApiService::class.java)
        val as2 = apiClient.create(As2::class.java)
        Assert.assertNotEquals(apiService, as2)
        Assert.assertNotEquals(apiClient.getRetrofit(), apiClient.getRetrofit("http://g2.com"))

    }
}