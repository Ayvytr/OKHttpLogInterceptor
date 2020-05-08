package com.ayvytr.coroutines.second

import android.os.Bundle
import androidx.lifecycle.Observer
import com.ayvytr.coroutine.BaseCoroutineActivity
import com.ayvytr.coroutine.viewmodel.BaseViewModel
import com.ayvytr.coroutines.R
import com.ayvytr.coroutines.main.MainViewModel
import com.ayvytr.ktx.ui.getContext
import com.ayvytr.ktx.ui.show
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SecondActivity : BaseCoroutineActivity<MainViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setContentView(R.layout.activity_second)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        getContext()
        setTitle("SecondActivity")
//        supportFragmentManager.beginTransaction()
//            .add(R.id.fl, SecondFragment(), SecondFragment::class.java.name)
//            .commit()
//        launch{
//            mViewModel.mLoadingLiveData.value = true
//            delay(2000)
//            mViewModel.mLoadingLiveData.value = false
//        }
        btn_get_data.setOnClickListener {
            mViewModel.getAndroidAndIos()
        }
        mViewModel.androidAndIosLiveData.observe(this, Observer {
            tv_value.text = it.toString()
        })
    }

    override fun showLoading(isShow: Boolean) {
        pb.show(isShow)
    }
}
