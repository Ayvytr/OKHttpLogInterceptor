package com.ayvytr.network

import android.content.Context
import android.net.ConnectivityManager
import com.ayvytr.network.provider.ContextProvider

/**
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 */

/**
 * 判断网络是否可用
 *
 * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`
 *
 * @return `true`: 可用<br></br>`false`: 不可用
 */
fun isNetworkAvailable(): Boolean {
    val cm = getConnectivityManager()
    val activeNetworkInfo = cm.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isAvailable
}

/**
 * Return [ConnectivityManager].
 */
fun getConnectivityManager(): ConnectivityManager {
    return ContextProvider.globalContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}

/**
 * 判断网络是否连接
 *
 * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`
 *
 * @return `true`: 是<br></br>`false`: 否
 */
fun isNetworkConnected(): Boolean {
    val cm = getConnectivityManager()
    val activeNetworkInfo = cm.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun isWifiConnected(): Boolean {
    val cm = getConnectivityManager()
    return cm.activeNetworkInfo?.type == ConnectivityManager.TYPE_WIFI
}

fun isMobileDataConnected(): Boolean {
    val cm = getConnectivityManager()
    return cm.activeNetworkInfo?.type == ConnectivityManager.TYPE_MOBILE
}
