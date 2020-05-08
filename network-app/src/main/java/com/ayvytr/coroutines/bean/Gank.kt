package com.ayvytr.coroutines.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author admin
 */
@Parcelize
data class Gank(
    /**
     * _id : 5bba1b899d212261127b79d1
     * createdAt : 2018-10-07T14:43:21.406Z
     * desc : Android自动屏幕适配插件，大大减轻你和UI设计师的工作量
     * images : ["https://ww1.sinaimg.cn/large/0073sXn7ly1fw0vipvym5j30ny09o758","https://ww1.sinaimg.cn/large/0073sXn7ly1fw0vipycxjj30gy09gt9j"]
     * publishedAt : 2018-10-08T00:00:00.0Z
     * source : web
     * type : Android
     * url : http://tangpj.com/2018/09/29/calces-screen/
     * used : true
     * who : PJ Tang
     */

    var _id: String? = null,
    var createdAt: String? = null,
    var desc: String? = null,
    var publishedAt: String? = null,
    var source: String? = null,
    var type: String? = null,
    var url: String? = null,
    var isUsed: Boolean = false,
    var who: String? = null,
    var images: List<String>? = null,
    var isHeader: Boolean = false
) : Parcelable {
    constructor(type: String, isHeader: Boolean) : this() {
        this.type = type
        this.isHeader = isHeader
    }

    fun compareByAndroid(newItem: Gank):Boolean {
        return _id == newItem._id &&
                desc == newItem.desc &&
                publishedAt == newItem.publishedAt &&
                url == newItem.url &&
                who == newItem.who
    }

}
