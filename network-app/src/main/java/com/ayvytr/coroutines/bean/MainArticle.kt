package com.ayvytr.wanandroid.bean

import com.ayvytr.coroutines.bean.Article

/**
 * @author ayvytr
 */
data class MainArticle(
    val curPage: Int,
    var datas: List<Article>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
) {
    fun hasMore(): Boolean {
        return pageCount > curPage
    }
}

