package com.ayvytr.coroutines.api

import com.ayvytr.coroutines.bean.BaseGank
import com.ayvytr.coroutines.bean.Gank
import com.ayvytr.network.bean.ResponseWrapper
import com.ayvytr.wanandroid.bean.BaseData
import com.ayvytr.wanandroid.bean.MainArticle
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

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

    @GET("wenda/list/{page}/json")
    suspend fun askArticle(@Path("page") page: Int): BaseData<MainArticle>

    //以上是干货网链接

    @GET("hotkey/json")
    suspend fun getHotKey(): ResponseWrapper<String>

    @GET
    suspend fun downloadWanAndroidApp(@Url url:String  = "https://wanandroid.com/blogimgs/2d120094-e1ee-47fb-a155-6eb4ca49d01f.apk")
            : ResponseWrapper<ByteArray>
}
