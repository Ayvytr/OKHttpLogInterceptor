package com.ayvytr.coroutines.main

import com.ayvytr.coroutines.bean.BaseGank
import com.ayvytr.flow.base.IView

/**
 * @author Administrator
 */
interface MainView: IView {
    fun onHotKey(it: BaseGank)
}