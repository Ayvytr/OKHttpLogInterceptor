package com.ayvytr.networkapp

import android.app.Application
import com.ayvytr.network.ApiClient
import com.ayvytr.okhttploginterceptor.Priority

/**
 * @author admin
 */

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        //初始化，默认开启了OKhttp缓存，cache=null关闭
//        ApiClient.init("https://gank.io/api/", cache = null)
        ApiClient.init("https://www.wanandroid.com/")
//        ApiClient.getInstance().logInterceptor.showLog = false
        val logInterceptor = ApiClient.logInterceptor
        logInterceptor.priority = Priority.E
        logInterceptor.visualFormat = false
        logInterceptor.isShowAll = true
//        L.settings().showLog(BuildConfig.DEBUG)
        //覆盖重写自定义全局网络异常转为ResponseMessage
//        ApiClient.throwable2ResponseMessage = {
//            ResponseMessage("哈哈", throwable = it)
//        }

        logInterceptor.ignoreLongBody = false
    }
}