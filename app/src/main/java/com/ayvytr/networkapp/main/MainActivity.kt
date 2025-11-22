package com.ayvytr.networkapp.main

import android.R.attr.text
import android.app.ProgressDialog.show
import android.os.Bundle
import com.ayvytr.networkapp.R
import com.ayvytr.networkapp.bean.BaseGank
import com.ayvytr.flow.BaseActivity
import com.ayvytr.ktx.ui.hide
import com.ayvytr.ktx.ui.show
import com.ayvytr.logger.L
import com.ayvytr.networkapp.databinding.ActivityMainBinding

class MainActivity : BaseActivity<MainViewModel>(), MainView {

    lateinit var binding: ActivityMainBinding

    override fun showLoading(isShow: Boolean) {
        binding.pb.show(isShow)
    }

//    override fun getViewModelClass(): Class<MainViewModel> {
//        return MainViewModel::class.java
//    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onHotKey(it: BaseGank) {
        binding.tvValue.text = it.toString()
    }

    override fun initData(savedInstanceState: Bundle?) {

        binding.btnGetData.setOnClickListener {
            viewModel.getHotKey()
        }

    }

    override fun showMessage(message: CharSequence) {
        super.showMessage(message)
        L.e("errorLiveData", message)
        binding.tvError.text = message
        binding.pb.hide()
    }

    override fun initViewModel() {
        super.initViewModel()

    }
}
