package com.ayvytr.network

import com.ayvytr.network.ApiClient.baseUrl
import com.ayvytr.network.ApiClient.get
import com.ayvytr.network.ApiClient.init
import com.ayvytr.network.ApiClient.logInterceptor
import com.ayvytr.network.ApiClient.of
import com.ayvytr.network.ApiClient.okHttpClient
import com.ayvytr.okhttploginterceptor.LoggingInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.collections.set

/**
 * [Retrofit]和[OkHttpClient]管理类，初始化：[init]，获取[Retrofit]：[of], 获取api service：[get].
 *
 * [init]有2种方法初始化.

 * 自定义初始化请使用[init]自行传入[baseUrl], [okHttpClient], [Retrofit].
 *
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 3.0.2
 * 修改[LoggingInterceptor.showLog]=BuildConfig.Debug不显示log的问题. release包BuildConfig.Debug=false
 * @since 3.0.1
 * 1. 修改[init]中interceptors不是默认值时[LoggingInterceptor]丢失问题
 * 2. 修改[logInterceptor]默认Debug显示所有log，包括请求头信息
 * 3. [init]增加[JvmStatic]
 *
 * @since 3.0.0
 * 1. 改版和拆分，只做基础功能，cookie和cache移到扩展包提供。[of]获取[Retrofit]，[get]获取api实例
 * 2. 拆分cookie, cache功能
 * 3. 删除ResponseWrapper等无意义封装
 *
 * @since 2.3.0 抛弃getInstance和单例类做法，直接使用object [ApiClient] 进行初始化和使用.
 */
object ApiClient {
    lateinit var okHttpClient: OkHttpClient
    lateinit var baseUrl: String

    private val retrofitMap = hashMapOf<String, Retrofit>()

    val logInterceptor by lazy { LoggingInterceptor(true, true) }


    /**
     * 自定义初始化[OkHttpClient].
     */
    @JvmStatic
    fun init(baseUrl: String, okHttpClient: OkHttpClient, retrofit: Retrofit) {
        this.okHttpClient = okHttpClient
        retrofitMap[baseUrl] = retrofit
        this.baseUrl = baseUrl
    }

    /**
     * 初始化方法.
     */
    @JvmStatic
    @JvmOverloads
    fun init(
        baseUrl: String,
        okHttpTimeoutSeconds: Int = 10,
        interceptors: List<Interceptor> = listOf(),
        networkInterceptors: List<Interceptor> = listOf(),
        converterFactories: List<Converter.Factory> = listOf(GsonConverterFactory.create()),
        callAdapterFactories: List<CallAdapter.Factory> = listOf()
    ) {
        val longOkHttpTimeoutSeconds = okHttpTimeoutSeconds.toLong()
        okHttpClient = OkHttpClient.Builder()
            .apply {
                connectTimeout(longOkHttpTimeoutSeconds, TimeUnit.SECONDS)
                readTimeout(longOkHttpTimeoutSeconds, TimeUnit.SECONDS)
                writeTimeout(longOkHttpTimeoutSeconds, TimeUnit.SECONDS)

                addInterceptor(logInterceptor)

                interceptors.map {
                    addInterceptor(it)
                }
                networkInterceptors.map {
                    addNetworkInterceptor(it)
                }
            }
            .build()


        val defaultRetrofit = Retrofit.Builder()
            .apply {
                baseUrl(baseUrl)
                client(okHttpClient)

                converterFactories.map {
                    addConverterFactory(it)
                }
                callAdapterFactories.map {
                    addCallAdapterFactory(it)
                }
            }
            .build()

        this.baseUrl = baseUrl
        retrofitMap[baseUrl] = defaultRetrofit
    }

    /**
     * 根据[url]获取[Retrofit]
     */
    fun of(url: String = this.baseUrl): Retrofit {
        var retrofit = retrofitMap[url]
        if (retrofit == null) {
            retrofit = retrofitMap[baseUrl]!!.newBuilder()
                .baseUrl(url)
                .client(okHttpClient)
                .build()
            retrofitMap[url] = retrofit
        }

        return retrofit!!
    }

    /**
     * 获取[Retrofit]创建的api service
     */
    fun <T> get(service: Class<T>, baseUrl: String = this.baseUrl): T {
        val retrofit = of(baseUrl)
        return retrofit.create(service)
    }

}

