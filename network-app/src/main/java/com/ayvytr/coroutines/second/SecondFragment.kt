package com.ayvytr.coroutines.second

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ayvytr.coroutine.BaseFragment
import com.ayvytr.coroutine.viewmodel.BaseViewModel
import com.ayvytr.coroutines.R
import com.ayvytr.ktx.ui.show
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author EDZ
 */
class SecondFragment : BaseFragment<BaseViewModel>() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_main, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }

    override fun showLoading(isShow: Boolean) {
        pb.show(isShow)
    }
}