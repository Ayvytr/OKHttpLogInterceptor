package com.ayvytr.coroutines.api

import com.ayvytr.coroutines.bean.BaseGank
import com.ayvytr.coroutines.bean.Gank
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

/**
 * @author EDZ
 */
interface Api {
    @GET("data/iOS/2/1")
    suspend fun getIosGank(): BaseGank

    @GET("data/Android/2/1")
    suspend fun getAndroidGank(): BaseGank

    @GET("data/iOS/2/1")
    fun getIosGankDeferred(): Deferred<BaseGank>

    @GET("data/Android/2/1")
    fun getAndroidGankDeferred(): Deferred<BaseGank>

}
