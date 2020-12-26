package com.ayvytr.coroutines.multitask

import android.os.Bundle
import androidx.lifecycle.Observer
import com.ayvytr.coroutine.BaseActivity
import com.ayvytr.coroutines.R
import kotlinx.android.synthetic.main.activity_multi_network_task.*

class MultiNetworkTaskActivity: BaseActivity<MultiNetworkTaskViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_network_task)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        btn_request.setOnClickListener {
//            mViewModel.getAndroidAndIos()
            mViewModel.getAndroidGank()
        }
    }

    override fun initViewModel() {
        super.initViewModel()
//        mViewModel.androidAndIosLiveData.observe(this, Observer {
        mViewModel.androidGankLiveData.observe(this, Observer {
            tv.setText(it.toString())
        })
    }
}