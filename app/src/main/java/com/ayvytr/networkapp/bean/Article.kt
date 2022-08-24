package com.ayvytr.networkapp.bean


/**
 * 其中有两个易混淆的字段:
 * "superChapterId": 153,
 * "superChapterName": "framework", // 一级分类的名称
 * superChapterId其实不是一级分类id，因为要拼接跳转url，内容实际都挂在二级分类下，所以该id实际上是一级分类的第一个子类目的id，拼接后故可正常跳转。

 * 有两个字段比较容易混淆：
 * author 与 shareUser
 * 网站上的文章可能是某位作者author的，也可能是某位分享人shareUser分享的。
 * 如果是分享人分享的，author 为 null。
 * 注意：除了文字标题，链接，其他字段都可能为null，一定要注意布局下发 null 时的显示情况。
 *
 */
data class Article(
    val id: Int,
    val title: String,
    val link: String,
    val apkLink: String?,
    val audit: Int,
    val author: String?,
    val canEdit: Boolean,
    val chapterId: Int,
    val chapterName: String,
    val collect: Boolean,
    val courseId: Int,
    val desc: String?,
    val descMd: String?,
    val envelopePic: String?,
    val fresh: Boolean,
    val niceDate: String,
    val niceShareDate: String,
    val origin: String?,
    val prefix: String?,
    val projectLink: String?,
    val publishTime: Long,
    val selfVisible: Int,
    val shareDate: Long,
    val shareUser: String?,
    val superChapterId: Int,
    val superChapterName: String,
//    val tags: List<Tag>,
    val type: Int,
    val userId: Int,
    val visible: Int,
    val zan: Int
)