package com.ayvytr.coroutines.multitask

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ayvytr.coroutine.viewmodel.BaseViewModel
import com.ayvytr.coroutines.api.Api
import com.ayvytr.coroutines.bean.BaseGank
import com.ayvytr.coroutines.bean.Gank
import com.ayvytr.coroutines.main.MainRepository
import com.ayvytr.network.ApiClient
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MultiNetworkTaskViewModel: BaseViewModel() {
    private val repository = MainRepository()
    val androidGankLiveData = MutableLiveData<BaseGank>()
    val iosGankLiveData = MutableLiveData<BaseGank>()
    val androidAndIosLiveData = MutableLiveData<List<Gank>>()

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
            val android = async { repository.getAndroidGank() }.await()
            val ios = async { repository.getIosGank() }.await()
            val list = android.results!!.toMutableList()
            list.addAll(ios.results!!)
            androidAndIosLiveData.value = list

        }
    }


}
