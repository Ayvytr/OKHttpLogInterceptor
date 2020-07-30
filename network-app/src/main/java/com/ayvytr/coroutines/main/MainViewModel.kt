package com.ayvytr.coroutines.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ayvytr.coroutine.viewmodel.BaseViewModel
import com.ayvytr.coroutines.api.WanApi
import com.ayvytr.coroutines.bean.BaseGank
import com.ayvytr.coroutines.bean.Gank
import com.ayvytr.logger.L
import com.ayvytr.network.ApiClient
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

    private val wanApi = ApiClient.getRetrofit("https://www.wanandroid.com/").create(WanApi::class.java)

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
        viewModelScope.launch {
            wanApi.askArticle(1)
        }
    }
}
