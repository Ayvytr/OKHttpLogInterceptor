package com.ayvytr.coroutines.t

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

/**
 * @author EDZ
 */
abstract class BaseActivity3<T : BaseJavaT> :
    AppCompatActivity() {
    @JvmField
    protected var t: T? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vmClass = getViewModelClass()
        if (vmClass != null) {
            t = ViewModelProvider(this)[vmClass]
        }
    }

    abstract fun getViewModelClass(): Class<T>?

}