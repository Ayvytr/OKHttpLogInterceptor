package com.ayvytr.network

import com.ayvytr.network.ApiClient.baseUrl
import com.ayvytr.network.ApiClient.get
import com.ayvytr.network.ApiClient.init
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
 * 创建Retrofit Api接口入口类，使用[of],[get]之前，一定要调用[init]初始化.
 * 默认[init]提供了baseUrl, 默认10s超时，拦截器，等参数。

 * 自定义初始化请使用[init]自行传入[baseUrl], [okHttpClient], [Retrofit].
 *
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 3.0.0
 * 改版和拆分，只做基础功能，cookie和cache移到扩展包提供。[of]获取[Retrofit]，[get]获取api实例
 *
 * 拆分cookie, cache功能
 *
 * 删除ResponseWrapper等无意义封装
 *
 * @since 2.3.0 抛弃getInstance和单例类做法，直接使用object [ApiClient] 进行初始化和使用.
 */
object ApiClient {
    lateinit var okHttpClient: OkHttpClient
    lateinit var defaultRetrofit: Retrofit
    lateinit var baseUrl: String

    private val retrofitMap = hashMapOf<String, Retrofit>()

    val logInterceptor by lazy { LoggingInterceptor() }


    /**
     * 自定义初始化[OkHttpClient].
     */
    fun init(baseUrl: String, okHttpClient: OkHttpClient, retrofit: Retrofit) {
        this.okHttpClient = okHttpClient
        defaultRetrofit = retrofit
        retrofitMap[baseUrl] = defaultRetrofit
        this.baseUrl = baseUrl
    }

    /**
     * 初始化方法.
     */
    @JvmOverloads
    fun init(
        baseUrl: String,
        okHttpTimeoutSeconds: Int = 10,
        interceptors: List<Interceptor> = listOf(LoggingInterceptor(BuildConfig.DEBUG)),
        networkInterceptors: List<Interceptor> = listOf(),
        converterFactories: List<Converter.Factory> = listOf(GsonConverterFactory.create()),
        callAdapterFactories: List<CallAdapter.Factory> = listOf()
    ) {
        val longOkHttpTimeoutSeconds = okHttpTimeoutSeconds.toLong()
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .connectTimeout(longOkHttpTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(longOkHttpTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(longOkHttpTimeoutSeconds, TimeUnit.SECONDS)
            .apply {
                interceptors.forEach {
                    addInterceptor(it)
                }
                networkInterceptors.forEach {
                    addNetworkInterceptor(it)
                }
            }
            .build()


        defaultRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .apply {
                converterFactories.forEach {
                    addConverterFactory(it)
                }
                callAdapterFactories.forEach {
                    addCallAdapterFactory(it)
                }
            }
            .build()

        this.baseUrl = baseUrl
        retrofitMap[baseUrl] = defaultRetrofit
    }

    fun of(baseUrl: String = this.baseUrl): Retrofit {
        if (baseUrl == this.baseUrl) {
            return defaultRetrofit
        }

        var retrofit = retrofitMap[baseUrl]
        if (retrofit == null) {
            retrofit = this.defaultRetrofit.newBuilder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build()
            retrofitMap[baseUrl] = retrofit
        }

        return retrofit!!
    }

    fun <T> get(service: Class<T>, baseUrl: String = this.baseUrl): T {
        val retrofit = of(baseUrl)
        return retrofit.create(service)
    }

}

