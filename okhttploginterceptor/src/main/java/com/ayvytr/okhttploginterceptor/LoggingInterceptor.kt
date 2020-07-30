package com.ayvytr.okhttploginterceptor

import android.util.Log
import com.ayvytr.okhttploginterceptor.printer.DefaultLogPrinter
import com.ayvytr.okhttploginterceptor.printer.IPrinter
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * OkHttp拦截器，有打印请求头，请求体，响应头，响应体的功能，3.0.0开始精简了配置，打印模式精简为2种，详细配置
 * 见参数说明.
 * @param showLog 是否显示Log
 * @param isShowAll `false`: 打印除请求参数，请求头，响应头的所有内容。`true`：打印所有内容
 * @param tag Log的tag
 * @param priority [Log]的优先级
 * @param visualFormat `true`: 格式化json和xml字符串；`false`：仅控制每行最大长度
 * @param maxLineLength 每行最大字符串数量
 * @param printer 额外自定义处理Log
 *
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 3.0.3 增加[requestTag],[responseTag]，区分请求和响应的tag
 *              修改打印逻辑为异步打印，解决请求半秒钟，打印5秒钟的问题
 * @since 3.0.2 取消moreAction，修改为[IPrinter]作为自定义log接口
 *              重写[separateByLength], [visualFormat]=false时，限制每行最大长度的同时，不定长每行长度，
 *              以separateChars中 ',', ' '等字符作为每行最后一个字符，以减少有效字符串被截成两行的问题
 *
 * @since 3.0.1 修改不同请求log被分割问题
 * @since 3.0.0 全新改版，取消以前的多种打印模式，最大化精简配置；对json，xml格式化打印，增强了可读性；取消
 *              使用OkHttp的Log打印，改为系统的[Log]
 * @since 1.0.0
 */
class LoggingInterceptor @JvmOverloads constructor(var showLog: Boolean = true,
                                                   var isShowAll: Boolean = false,
                                                   var tag: String = DEFAULT_TAG,
                                                   var priority: Priority = Priority.I,
                                                   var visualFormat: Boolean = true,
                                                   var maxLineLength: Int = MAX_LINE_LENGTH,
                                                   var printer: IPrinter? = null):
    Interceptor {
    var requestTag = "$tag-$REQUEST"
    var responseTag = "$tag-$RESPONSE"

    init {
        if (maxLineLength <= 0) {
            maxLineLength = MAX_LINE_LENGTH
        }
    }

    private val defaultPrinter = DefaultLogPrinter()

    private val requestExecutor = Executors.newSingleThreadExecutor()
    private val responseExecutor = Executors.newSingleThreadExecutor()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return logIntercept(chain)
    }

    @Throws(IOException::class)
    private fun logIntercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (!showLog) {
            return chain.proceed(request)
        }

        printRequest(request)

        val startNs = System.nanoTime()

        val response: Response
        response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            printException(request, e)
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        printResponse(response, tookMs)
        return response
    }

    private fun printException(request: Request, e: IOException) {
        val list = mutableListOf<String>()
        val starter = "${LT}[Response][${request.method}] ${request.url} ".appendLine()
        list.add(starter)
        list.add("$L Exception:$e")
        list.add(FOOTER)
        printResponseList(list)
    }

    private fun printResponse(response: Response, tookMs: Long) {
        val request = response.request
        val responseBody = response.body

        val list = mutableListOf<String>()
        val starter = "${LT}[Response][${request.method} ${response.code} ${response.message} ${tookMs}ms] ${request.url} ".appendLine()
        list.add(starter)

        val headers = response.headers
        if (isShowAll) {
            list.add("$L Protocol: ${response.protocol}")
            headers.forEach {
                list.add("$L ${it.first}: ${it.second}")
            }

            responseBody?.apply {
                contentType()?.let {
                    if (headers["Content-Type"] == null) {
                        list.add("$L Content-Type: $it")
                    }
                }
                if (contentLength() != -1L) {
                    if (headers["Content-Length"] == null) {
                        list.add("$L Content-Length: ${contentLength()}")
                    }
                }
            }
        }

        responseBody?.also {
            val peekBody = response.peekBody(Long.MAX_VALUE)
            if (isShowAll && responseBody.contentLength() == -1L) {
                list.add("$L Content-Length: ${peekBody.contentLength()}")
            }

            list.add("$L Body:")

            list.addAll(peekBody.formatAsPossible(visualFormat, maxLineLength).map {
                "$L $it"
            })
        }

        list.add(FOOTER)

        printResponseList(list)
    }

    private fun printRequest(request: Request) {
        val requestBody = request.body

        val list = mutableListOf<String>()
        val header = "${LT}[Request][${request.method}] ${request.url} ".appendLine()
        list.add(header)

        val headers = request.headers
        if (isShowAll) {
            val querySize = request.url.querySize
            if (querySize > 0) {
                list.add("$L Query Parameters: ${request.url.query}")
            }

            list.addAll(headers.map {
                "$L ${it.first}: ${it.second}"
            })

            requestBody?.apply {
                contentType()?.let {
                    if (headers["Content-Type"] == null) {
                        list.add("$L Content-Type: $it")
                    }
                }
                if (contentLength() != -1L) {
                    if (headers["Content-Length"] == null) {
                        list.add("$L Content-Length: ${contentLength()}")
                    }
                }
            }
        }

        requestBody?.also {
            if (bodyHasUnknownEncoding(request.headers) ||
                    requestBody.isDuplex() ||
                    requestBody.isOneShot()) {
                list.add(BODY_OMITTED)
            } else {
                list.add("$L Body:")

                list.addAll(requestBody.formatAsPossible(visualFormat, maxLineLength).map {
                    "$L $it"
                })

                list.add(FOOTER)
            }
        } ?: list.add(FOOTER)

        printRequestList(list)
    }

    private fun printRequestList(list: MutableList<String>) {
        requestExecutor.execute {
            list.forEach {
                print(requestTag, it)
            }
        }
    }

    private fun printResponseList(list: MutableList<String>) {
        responseExecutor.execute {
            list.forEach {
                print(responseTag, it)
            }
        }
    }


    /**
     * 打印字符串.
     *
     * 为了解决在 AndroidStudio v3.1 以上 Logcat 输出的日志无法对齐的问题
     * <p>
     * 此问题引起的原因, 据 JessYan 猜测, 可能是因为 AndroidStudio v3.1 以上将极短时间内以相同 tag 输出
     * 多次的 log 自动合并为一次输出，导致本来对称的输出日志, 出现不对称的问题.
     * AndroidStudio v3.1 此次对输出日志的优化, 不小心使市面上所有具有日志格式化输出功能的日志框架无法正常工作
     * 现在暂时能想到的解决方案有两个: 1. 改变每行的 tag (每行 tag 都加一个可变化的 token) 2. 延迟每行日志打印的间隔时间
     *
     * 目前随机sleep 0-2ms，应能解决同时超多行log最后n行丢失的问题.
     *
     * @param msg 要打印的字符串
     */
    private fun print(tag: String, msg: String) {
        val millisecond = Random.nextInt(0, 3)
        Thread.sleep(millisecond.toLong())
        defaultPrinter.print(priority, tag, msg)
        printer?.print(priority, tag, msg)
    }


    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }

    companion object {
        //一行字符最大数量
        const val MAX_LINE_LENGTH = 1024
        const val LT = "┏"
        const val FOOTER = "┗[END]━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        const val LB = "┗"
        const val BODY_OMITTED = "┗[END]Body Omitted━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        const val L = "┃"
        const val CLINE = "━"

        const val DEFAULT_TAG = "OkHttp"

        const val REQUEST = "Request"
        const val RESPONSE = "Response"
    }

}
