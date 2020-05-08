package com.ayvytr.commonlibrary.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author admin
 */
@Parcelize
open class BaseGank(

    /**
     * error : false
     * results : [{"_id":"5bba1b899d212261127b79d1","createdAt":"2018-10-07T14:43:21.406Z","desc":"Android自动屏幕适配插件，大大减轻你和UI设计师的工作量","images":["https://ww1.sinaimg.cn/large/0073sXn7ly1fw0vipvym5j30ny09o758","https://ww1.sinaimg.cn/large/0073sXn7ly1fw0vipycxjj30gy09gt9j"],"publishedAt":"2018-10-08T00:00:00.0Z","source":"web","type":"Android","url":"http://tangpj.com/2018/09/29/calces-screen/","used":true,"who":"PJ Tang"},{"_id":"5bbb01d29d2122610aba3458","createdAt":"2018-10-08T07:05:54.881Z","desc":"PixelShot是一个非常棒的Android库，可以将您应用中的任何视图保存为图像","publishedAt":"2018-10-08T00:00:00.0Z","source":"chrome","type":"Android","url":"https://github.com/Muddz/PixelShot","used":true,"who":"lijinshanmx"},{"_id":"5bbb01f69d2122610ee409d7","createdAt":"2018-10-08T07:06:30.814Z","desc":"安卓平台下，图片或视频转化为ascii，合并视频用到ffmpeg库。后期会加入带色彩的ascii码图片或视频","images":["https://ww1.sinaimg.cn/large/0073sXn7ly1fw0viqnrjvj30u01hctcg","https://ww1.sinaimg.cn/large/0073sXn7ly1fw0viry9ksg30uk1ib7wn"],"publishedAt":"2018-10-08T00:00:00.0Z","source":"chrome","type":"Android","url":"https://github.com/GodFengShen/PicOrVideoToAscii","used":true,"who":"lijinshanmx"},{"_id":"5bbb02069d21226111b86f0e","createdAt":"2018-10-08T07:06:46.371Z","desc":"高仿抖音照片电影功能。","images":["https://ww1.sinaimg.cn/large/0073sXn7ly1fw0vit6hxhg30a00hs1l1","https://ww1.sinaimg.cn/large/0073sXn7ly1fw0viu5glgg30a00ktu0z"],"publishedAt":"2018-10-08T00:00:00.0Z","source":"chrome","type":"Android","url":"https://github.com/yellowcath/PhotoMovie","used":true,"who":"lijinshanmx"},{"_id":"5bbb04139d21226111b86f10","createdAt":"2018-10-08T07:15:31.553Z","desc":"flutter自定义波浪view.","images":["https://ww1.sinaimg.cn/large/0073sXn7ly1fw0viui2f0g30740aoayh"],"publishedAt":"2018-10-08T00:00:00.0Z","source":"chrome","type":"Android","url":"https://github.com/While1true/WaveView_flutter","used":true,"who":"lijinshanmx"},{"_id":"5bbb07ba9d2122610aba345a","createdAt":"2018-10-08T07:31:06.287Z","desc":"Flutter图片选择器。","images":["https://ww1.sinaimg.cn/large/0073sXn7ly1fw0viumciuj305k0c1ta4","https://ww1.sinaimg.cn/large/0073sXn7ly1fw0viur17lj305k0c1jtl","https://ww1.sinaimg.cn/large/0073sXn7ly1fw0viutmagj305k09w0tk","https://ww1.sinaimg.cn/large/0073sXn7ly1fw0viuxayxj305k09wgmo"],"publishedAt":"2018-10-08T00:00:00.0Z","source":"chrome","type":"Android","url":"https://github.com/Sh1d0w/multi_image_picker","used":true,"who":"lijinshanmx"},{"_id":"5bbb07d19d2122610aba345b","createdAt":"2018-10-08T07:31:29.33Z","desc":"仿android安卓抖音v2.5加载框控件。","images":["https://ww1.sinaimg.cn/large/0073sXn7ly1fw0vivbxuxg30hs0xke81","https://ww1.sinaimg.cn/large/0073sXn7ly1fw0vivpvwpg30hs0xku0x"],"publishedAt":"2018-10-08T00:00:00.0Z","source":"chrome","type":"Android","url":"https://github.com/CCY0122/douyinloadingview","used":true,"who":"lijinshanmx"},{"_id":"5bbb07ea9d2122610aba345c","createdAt":"2018-10-08T07:31:54.85Z","desc":"适用于Android的轻巧且易于使用的Audio Visualizer。","images":["https://ww1.sinaimg.cn/large/0073sXn7ly1fw0vivxuzjg308w0cjq6f","https://ww1.sinaimg.cn/large/0073sXn7ly1fw0viw1541g308w0d1dm1","https://ww1.sinaimg.cn/large/0073sXn7ly1fw0viw3xurg308w0czte0","https://ww1.sinaimg.cn/large/0073sXn7ly1fw0viw79f6g308w0bwk0k"],"publishedAt":"2018-10-08T00:00:00.0Z","source":"chrome","type":"Android","url":"https://github.com/gauravk95/audio-visualizer-android","used":true,"who":"lijinshanmx"},{"_id":"5bbb08259d2122610ee409db","createdAt":"2018-10-08T07:32:53.505Z","desc":"Android仿火币K线图实现。","images":["https://ww1.sinaimg.cn/large/0073sXn7ly1fw0viwdt0uj30u01hctdn","https://ww1.sinaimg.cn/large/0073sXn7ly1fw0viwmyw0j30u01hcaco"],"publishedAt":"2018-10-08T00:00:00.0Z","source":"chrome","type":"Android","url":"https://github.com/fujianlian/KLineChart","used":true,"who":"lijinshanmx"},{"_id":"5b977a759d212206c1b383d3","createdAt":"2018-09-11T08:19:01.268Z","desc":"手把手教你实现抖音视频特效","publishedAt":"2018-09-19T00:00:00.0Z","source":"web","type":"Android","url":"https://www.jianshu.com/p/5bb7f2a0da90","used":true,"who":"xue5455"}]
     */

    var isError: Boolean = false,
    var results: List<Gank>? = null
) : Parcelable {
    fun isSucceed(): Boolean {
        return !isError
    }
}
