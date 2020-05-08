package com.ayvytr.coroutines.t

import android.os.Bundle
import com.ayvytr.coroutines.R

/**
 * @author EDZ
 */
class ChildActivity3 : BaseActivity3<ChildJavaT>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        t!!.childFunction()
    }

    override fun getViewModelClass(): Class<ChildJavaT>? {
        return ChildJavaT::class.java
    }

//    override fun <ChildJavaT> getViewModelClass(): Class<ChildJavaT>? {
//        return ChildJavaT::class.java
//    }

}