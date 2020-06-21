package com.ayvytr.network

import android.os.Environment
import com.ayvytr.network.ApiClient.init
import com.ayvytr.network.ApiClient.initCustom
import com.ayvytr.network.ApiClient.okHttpClient
import com.ayvytr.network.bean.BaseResponse
import com.ayvytr.network.exception.ResponseException
import com.ayvytr.network.interceptor.CacheInterceptor
import com.ayvytr.network.interceptor.CacheNetworkInterceptor
import com.ayvytr.network.provider.ContextProvider
import com.ayvytr.okhttploginterceptor.LoggingInterceptor
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/**
 * 创建Retrofit Api接口入口类，默认[init]提供了baseUrl, 默认10s超时，拦截器，缓存等参数，也提供了
 * [initCustom]，进行自定义初始化[okHttpClient], [Retrofit].
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 2.3.0 抛弃getInstance和单例类做法，直接使用object [ApiClient] 进行初始化和使用.
 */
object ApiClient {
    lateinit var okHttpClient: OkHttpClient
        private set
    private lateinit var defaultRetrofit: Retrofit
    lateinit var baseUrl: String
        private set
    private val retrofitMap: HashMap<String, Retrofit> = hashMapOf()

    val logInterceptor by lazy { LoggingInterceptor() }

    val DEFAULT_CACHE by lazy { Cache(File(DEFAULT_CACHE_DIR, "okhttp"), 1024 * 1024 * 64) }
    val DEFAULT_CACHE_DIR by lazy {
        val context = ContextProvider.globalContext

        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() ||
                !Environment.isExternalStorageRemovable()) {
            context.externalCacheDir!!
        } else {
            context.cacheDir
        }
    }

    val cookieJar: PersistentCookieJar by lazy {
        PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(ContextProvider.globalContext))
    }

    fun initCustom(baseUrl: String, okHttpClientFunc: () -> OkHttpClient,
                   retrofitFunc: () -> Retrofit) {
        this.okHttpClient = okHttpClientFunc()
        defaultRetrofit = retrofitFunc()
        retrofitMap[baseUrl] = defaultRetrofit
        this.baseUrl = baseUrl
    }

    /**
     * Init [ApiClient].
     * @param cache if null, no cache
     */
    @JvmOverloads
    fun init(
        baseUrl: String,
        okHttpTimeoutSeconds: Int = 10,
        interceptorList: List<Interceptor> = listOf(),
        cache: Cache? = DEFAULT_CACHE,
        enableCookieJar: Boolean = false,
        cacheMaxAgeSeconds: Int = 3600
    ) {
        val longOkHttpTimeoutSeconds = okHttpTimeoutSeconds.toLong()
        val builder = OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .connectTimeout(longOkHttpTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(longOkHttpTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(longOkHttpTimeoutSeconds, TimeUnit.SECONDS)

        if (cache != null) {
            /**
             * 两个拦截器内容相同，主要解决了第一次请求网络没有缓存返回504的问题.
             * @see [https://stackoverflow.com/questions/23429046/can-retrofit-with-okhttp-use-cache-data-when-offline?r=SearchResults]
             */
            builder.cache(cache)
                .addInterceptor(CacheInterceptor(cacheMaxAgeSeconds))
                .addNetworkInterceptor(CacheNetworkInterceptor(cacheMaxAgeSeconds))
        }

        if(enableCookieJar) {
            builder.cookieJar(cookieJar)
        }

        interceptorList.map {
            builder.addInterceptor(it)
        }
        okHttpClient = builder.build()

        defaultRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        this.baseUrl = baseUrl
        retrofitMap[baseUrl] = defaultRetrofit
    }

    fun getRetrofit(baseUrl: String = this.baseUrl): Retrofit {
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

    fun <T> create(service: Class<T>, baseUrl: String = this.baseUrl): T {
        val retrofit = getRetrofit(baseUrl)
        return retrofit.create(service)
    }


//    private object SingletonHolder {
//        val NETWORK = ApiClient()
//    }

    //    companion object {


//    @JvmStatic
//    fun getDiskCacheDir(): File {
//
//    }

//        @JvmStatic
//        fun getInstance(): ApiClient {
//            return SingletonHolder.NETWORK
//        }

    /**
     * Convert Http throwable to [BaseResponse], override this to customize your response
     * message, string res and code.
     */
    @JvmField
    var throwable2ResponseMessage: (Throwable?) -> BaseResponse = {
        var message = ""
        var code = 0
        when (it) {
            is UnknownHostException -> message = "网络连接中断"
            is HttpException        -> {
                message = it.message()
                code = it.code()
            }
            else                    -> {
                message = it.toString()
                code = 0
            }
        }
        BaseResponse(
            false,
            ResponseException(message, code, -1, it)
        )
    }
//    }
}

