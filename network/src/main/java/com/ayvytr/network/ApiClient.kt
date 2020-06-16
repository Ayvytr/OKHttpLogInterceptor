package com.ayvytr.network

import android.os.Environment
import com.ayvytr.network.bean.ResponseMessage
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
 * Entry class of this library, use [ApiClient.getInstance] init, default, OkHttp has 10 seconds
 * timeout, default cache, and default cache max age by 3600 seconds.
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 */
class ApiClient private constructor() {
    lateinit var okHttpClient: OkHttpClient
        private set
    private lateinit var defaultRetrofit: Retrofit
    lateinit var baseUrl: String
        private set

    private val retrofitMap: HashMap<String, Retrofit> = hashMapOf()

    val logInterceptor = LoggingInterceptor()

    val cookieJar: PersistentCookieJar by lazy {
        PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(ContextProvider.globalContext))
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


    private object SingletonHolder {
        val NETWORK = ApiClient()
    }

    companion object {
        @JvmField
        val DEFAULT_CACHE: Cache = Cache(File(getDiskCacheDir(), "okhttp"), 1024 * 1024 * 64)

        @JvmStatic
        fun getDiskCacheDir(): File {
            val context = ContextProvider.globalContext
            return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() ||
                    !Environment.isExternalStorageRemovable()) {
                context.externalCacheDir!!
            } else {
                context.cacheDir
            }
        }

        @JvmStatic
        fun getInstance(): ApiClient {
            return SingletonHolder.NETWORK
        }

        /**
         * Convert Http throwable to [ResponseMessage], override this to customize your response
         * message, string res and code.
         */
        @JvmField
        var throwable2ResponseMessage: (Throwable?) -> ResponseMessage = {
            var message = ""
            var code = 0
            when (it) {
                is UnknownHostException -> message = "网络连接中断"
                is HttpException -> {
                    message = it.message()
                    code = it.code()
                }
                else -> {
                    message = it.toString()
                    code = 0
                }
            }
            ResponseMessage(
                message,
                code = code,
                throwable = it
            )
        }
    }
}

