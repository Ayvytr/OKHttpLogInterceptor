package com.ayvytr.coroutines.main

import android.os.Bundle
import androidx.lifecycle.Observer
import com.ayvytr.coroutine.BaseActivity
import com.ayvytr.coroutines.R
import com.ayvytr.ktx.ui.hide
import com.ayvytr.ktx.ui.show
import com.ayvytr.logger.L
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainViewModel>() {


    override fun showLoading(isShow: Boolean) {
        pb.show(isShow)
    }

//    override fun getViewModelClass(): Class<MainViewModel> {
//        return MainViewModel::class.java
//    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun initData(savedInstanceState: Bundle?) {
        mViewModel.androidAndIosLiveData.observe(this, Observer {
            tv_value.text = it.toString()
        })

        btn_get_data.setOnClickListener {
//            mViewModel.getAndroidAndIos()
//            mViewModel.downloadApp()
            mViewModel.getHotKey()
        }

//        mViewModel.getAndroidAndIos()
//        mViewModel.getAskArticle()
    }


    override fun showMessage(message: String) {
        super.showMessage(message)
        L.e("errorLiveData", message)
        tv_error.text = message
        pb.hide()
    }

    override fun initViewModel() {
        super.initViewModel()
        mViewModel.downloadAppLiveData.observe(this, Observer {
            L.e(it.data)
//            tv_value.setText(it.data)
        })
        mViewModel.hotKeyLiveData.observe(this, Observer {
            tv_value.setText(it.data)
        })
    }
}
