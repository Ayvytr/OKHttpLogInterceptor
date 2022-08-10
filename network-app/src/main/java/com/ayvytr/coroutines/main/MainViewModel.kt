package com.ayvytr.coroutines.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ayvytr.coroutines.api.Api
import com.ayvytr.coroutines.bean.BaseGank
import com.ayvytr.coroutines.bean.Gank
import com.ayvytr.flow.vm.BaseViewModel
import com.ayvytr.logger.L
import com.ayvytr.network.ApiClient
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * @author EDZ
 */
class MainViewModel: BaseViewModel<MainView>() {
    private val repository = MainRepository()

    private val api = ApiClient.of("https://www.wanandroid.com/").create(Api::class.java)


    fun getHotKey() {
        launchFlow({api.getHotKey()}, {view.onHotKey(it)})
    }
}
