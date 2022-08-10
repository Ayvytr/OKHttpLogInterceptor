package com.ayvytr.coroutines.main

import com.ayvytr.coroutines.api.Api
import com.ayvytr.coroutines.bean.BaseGank
import com.ayvytr.network.ApiClient

class MainRepository {
    private val api = ApiClient.get(Api::class.java)

    suspend fun getAndroidGank(): BaseGank {
        return api.getAndroidGank()
    }

    suspend fun getIosGank(): BaseGank {
        return api.getIosGank()
    }
}
