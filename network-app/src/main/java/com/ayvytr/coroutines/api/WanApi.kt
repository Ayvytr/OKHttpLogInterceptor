package com.ayvytr.coroutines.api

import com.ayvytr.wanandroid.bean.BaseData
import com.ayvytr.wanandroid.bean.MainArticle
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author Administrator
 */
interface WanApi {
    @GET("wenda/list/{page}/json")
    suspend fun askArticle(@Path("page") page: Int): BaseData<MainArticle>
}