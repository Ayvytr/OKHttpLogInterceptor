package com.ayvytr.networkapp.main

import com.ayvytr.networkapp.bean.BaseGank
import com.ayvytr.flow.base.IView

/**
 * @author Administrator
 */
interface MainView: IView {
    fun onHotKey(it: BaseGank)
}