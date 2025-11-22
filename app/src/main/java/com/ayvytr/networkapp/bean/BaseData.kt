package com.ayvytr.networkapp.bean


/**
 * @author ayvytr
 */
data class BaseData<T>(
    val `data`: T,
    val errorCode: Int,
    val errorMsg: String
) {
    fun isFailed(): Boolean {
        return errorCode != 0
    }

    fun isSucceed(): Boolean {
        return !isFailed()
    }

    fun throwFailedException() {
        if(isFailed()) {
            throw Exception(errorMsg)
        }
    }
}

