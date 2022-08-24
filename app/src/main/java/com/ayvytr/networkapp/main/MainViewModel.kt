package com.ayvytr.networkapp.main

import com.ayvytr.networkapp.api.Api
import com.ayvytr.flow.vm.BaseViewModel
import com.ayvytr.network.ApiClient

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
