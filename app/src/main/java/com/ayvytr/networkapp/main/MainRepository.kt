package com.ayvytr.networkapp.main

import com.ayvytr.networkapp.api.Api
import com.ayvytr.networkapp.bean.BaseGank
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
