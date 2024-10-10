package com.ayvytr.okhttploginterceptor

import android.util.Log
import com.ayvytr.okhttploginterceptor.printer.DefaultLogPrinter
import com.ayvytr.okhttploginterceptor.printer.IPrinter
import com.google.gson.GsonBuilder
import okhttp3.*
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
 * @since 3.0.7 修改列表forEach为map
 * @since 3.0.6 判断request，如果是[MultipartBody]，认为是文件，只打印基本信息，不打印body；修改默认长度
 *              （ignoreBodyIfMoreThan最大长度100KB）
 * @since 3.0.5 限制response body打印，只有contentType包含：text/xml/json/html/plain（认为可解析），
 *              并且没超出ignoreBodyIfMoreThan最大长度（默认16MB），才会打印response body，以解决例如
 *              下载文件body过大导致OOM的问题.
 * @since 3.0.4 优化解决了request和response log串行问题
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

    /**
     * 是否忽略过长的response body, 默认true；默认超过16MB，不打印response body.
     * 注意：[isParsable]，[ignoreLongBody]同时为真才会打印response body.
     *
     */
    var ignoreLongBody = true
    /**
     * 超过[ignoreBodyIfMoreThan]字节就忽略response body不打印，注意：这里单位是字节，默认字节数为16MB.
     */
    var ignoreBodyIfMoreThan = DEFAULT_IGNORE_LENGTH

    init {
        if (maxLineLength <= 0) {
            maxLineLength = MAX_LINE_LENGTH
        }
    }

    private val defaultPrinter = DefaultLogPrinter()

    private val singleExecutor = Executors.newSingleThreadExecutor()

    private fun canPrintBody(bodyLength: Int): Boolean {
        if (!ignoreLongBody) {
            return true
        }

        return ignoreLongBody && bodyLength < ignoreBodyIfMoreThan
    }

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
        //特别备注：别把printResponse放在异步执行，数据没拿完就close了
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
            if(headers.size > 0){
                list.add("$L Headers:")
            }
            headers.map {
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
            val peekBody = response.peekBody(1024)
            if (isShowAll && responseBody.contentLength() == -1L) {
                list.add("$L Content-Length: ${peekBody.contentLength()}")
            }

            peekBody.contentType()?.apply {
                /**
                 * 可解析，并且长度为超出，才打印response body. 不保险，判断不了[MultipartBody]上传文件
                 */
                if (isParsable() && canPrintBody(peekBody.contentLength().toInt())
                        && !isUnreadable()) {
                    list.add("$L Body:")
                    list.addAll(peekBody.formatAsPossible(visualFormat, maxLineLength).map {
                        "$L $it"
                    })
                    list.add(FOOTER)
                } else {
                    list.add(BODY_OMITTED)
                }
            }
        }

        printResponseList(list)
    }

    private fun printRequest(r: Request) {
        val request = r.newBuilder().build()
        val requestBody = request.body

        val list = mutableListOf<String>()
        val header = "${LT}[Request][${request.method}] ${request.url} ".appendLine()
        list.add(header)

        if (isShowAll) {
            val querySize = request.url.querySize
            if (querySize > 0) {
                list.add("$L Query: ${request.url.query}")
            }

            val headers = request.headers
            if(headers.size > 0){
                list.add("$L Headers:")
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
            if (it is MultipartBody) {
                list.add("$L Multipart: size=${it.parts.size}")
                it.parts.mapIndexed { i, part ->
                    val body = part.body
                    list.add("$L Multipart.parts[$i]: ${body.contentType()}; ${body.contentLength()}; headers:${part.headers}")
                }
                list.add(FOOTER)
            } else {
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
            }
        } ?: list.add(FOOTER)

        printRequestList(list)
    }

    private fun printRequestList(list: MutableList<String>) {
        singleExecutor.execute {
            list.map {
                print(requestTag, it)
            }
        }
    }

    private fun printResponseList(list: MutableList<String>) {
        singleExecutor.execute {
            list.map {
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
        const val BODY_OMITTED = "┗[END]Unreadable Body Omitted━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        const val L = "┃"
        const val CLINE = "━"

        const val DEFAULT_TAG = "OkHttp"

        const val REQUEST = "Request"
        const val RESPONSE = "Response"

        /**
         * @since 3.0.6 100KB
         * @since 3.0.5 16MB
         */
        const val DEFAULT_IGNORE_LENGTH = 102400

        val gson by lazy {
            GsonBuilder().setPrettyPrinting().create()
        }

    }

}
