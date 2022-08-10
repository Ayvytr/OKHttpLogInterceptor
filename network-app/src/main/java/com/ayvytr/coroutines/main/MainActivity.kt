package com.ayvytr.coroutines.main

import android.os.Bundle
import androidx.lifecycle.Observer
import com.ayvytr.coroutines.R
import com.ayvytr.coroutines.bean.BaseGank
import com.ayvytr.flow.BaseActivity
import com.ayvytr.ktx.ui.hide
import com.ayvytr.ktx.ui.show
import com.ayvytr.logger.L
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainViewModel>(), MainView {


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

    override fun onHotKey(it: BaseGank) {
        tv_value.text = it.toString()
    }

    override fun initData(savedInstanceState: Bundle?) {

        btn_get_data.setOnClickListener {
            viewModel.getHotKey()
        }

    }

    override fun showMessage(message: CharSequence) {
        super.showMessage(message)
        L.e("errorLiveData", message)
        tv_error.text = message
        pb.hide()
    }

    override fun initViewModel() {
        super.initViewModel()

    }
}
