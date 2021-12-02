package com.ayvytr.coroutines.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ayvytr.coroutine.viewmodel.BaseViewModel
import com.ayvytr.coroutines.api.Api
import com.ayvytr.coroutines.bean.BaseGank
import com.ayvytr.coroutines.bean.Gank
import com.ayvytr.logger.L
import com.ayvytr.network.ApiClient
import com.ayvytr.network.bean.ResponseWrapper
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * @author EDZ
 */
class MainViewModel : BaseViewModel() {
    private val repository = MainRepository()
    val androidGankLiveData = MutableLiveData<BaseGank>()
    val iosGankLiveData = MutableLiveData<BaseGank>()
    val androidAndIosLiveData = MutableLiveData<List<Gank>>()

    val downloadAppLiveData = MutableLiveData<ResponseWrapper<ByteArray>>()
    val hotKeyLiveData= MutableLiveData<ResponseWrapper<String>>()

    private val api = ApiClient.getRetrofit("https://www.wanandroid.com/").create(Api::class.java)

    fun getAndroidGank() {
        viewModelScope.launch {
            androidGankLiveData.value = repository.getAndroidGank()
        }
    }

    fun getIosGank() {
        viewModelScope.launch {
            iosGankLiveData.value = repository.getIosGank()
        }
    }

    fun getAndroidAndIos() {
        viewModelScope.launch {
            L.e(System.currentTimeMillis())
            val android = async { repository.getAndroidGank() }.await()
            L.e(System.currentTimeMillis())
            val ios = async { repository.getIosGank() }.await()
            L.e(System.currentTimeMillis())
            val list = android.results!!.toMutableList()
            list.addAll(ios.results!!)
            androidAndIosLiveData.value = list

        }
    }

    fun getAskArticle() {
        getAndroidAndIos()
    }

    fun downloadApp() {
        launchWrapper(downloadAppLiveData) {
            api.downloadWanAndroidApp()
        }
    }

    fun getHotKey() {
        launchWrapper(hotKeyLiveData){
            api.getHotKey()
        }
    }
}
