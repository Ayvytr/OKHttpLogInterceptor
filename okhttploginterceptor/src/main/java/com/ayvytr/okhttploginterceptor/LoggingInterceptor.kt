package com.ayvytr.okhttploginterceptor

import android.util.Log
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * [](https://github.com/square/okhttp) 拦截器，提供了打印状态，头，响应体等5种类型的log拦截打印方式.
 *
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 1.0.0
 * @param logType
 */
class LoggingInterceptor @JvmOverloads constructor(var logType: LogType = LogType.LEAST,
                                                   var showLog: Boolean = true,
                                                   var tag: String = "OkHttp",
                                                   var logPriority: LogPriority = LogPriority.V,
                                                   private val moreAction: (msg: String) -> Unit = {}) :
    Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return logIntercept(chain)
    }

    /**
     * 拦截日志方法.
     *
     * @param chain [Interceptor.Chain]
     * @return [Response]
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun logIntercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("test", "tv")
            .addHeader("test", "tv")
            .addHeader("test", "tv")
            .addHeader("test", "tv")
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
            .build()
        val startNs = System.nanoTime()

        if (logType == LogType.NONE || !showLog) {
            return chain.proceed(request)
        }

        printRequest(request)

        val response: Response
        response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            print(String.format("┣━━━ [HTTP EXCEPTION] url:%s %s", request.url, e.message))
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        printResponse(response, tookMs)
        return response
    }

    private fun printResponse(response: Response, tookMs: Long) {
        val isAll = logType == LogType.ALL
        val request = response.request
        val responseBody = response.body

        val starter = "${LT}[Response][${request.method} ${response.code} ${tookMs}ms] ${request.url} ".appendLine()
        print(starter)

        val headers = response.headers
        if (isAll) {
            headers.forEach{
                logHeader(it.first, it.second)
            }

            responseBody?.apply {
                contentType()?.let {
                    if (headers["Content-Type"] == null) {
                        print("$L Content-Type: $it")
                    }
                }
                if (contentLength() != -1L) {
                    if (headers["Content-Length"] == null) {
                        print("$L Content-Length: ${contentLength()}")
                    }
                }
            }

        }

        responseBody?.also {
            if (response.promisesBody() && !bodyHasUnknownEncoding(response.headers)) {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE) // Buffer the entire body.
                var buffer = source.buffer


                val contentType = responseBody.contentType()
                val charset: Charset = contentType?.charset(UTF8) ?: UTF8

                if (responseBody.contentLength() != 0L) {
                    val bodyStarter = "$L Body:"
                    print(bodyStarter)
                    print("$L ${buffer.clone().readString(charset)}")
                }

            }
        }

        print(FOOTER)
    }

    private fun printRequest(request: Request) {
        val requestBody = request.body
        val isAll = logType == LogType.ALL

        val header = "${LT}[Request][${request.method}] ${request.url} ".appendLine()
        print(header)

        val headers = request.headers
        if (isAll) {
            headers.forEach{
                logHeader(it.first, it.second)
            }

            // Request body headers are only present when installed as a network interceptor. When not
            // already present, force them to be included (if available) so their values are known.
            requestBody?.apply {
                contentType()?.let {
                    if (headers["Content-Type"] == null) {
                        print("$L Content-Type: $it")
                    }
                }
                if (contentLength() != -1L) {
                    if (headers["Content-Length"] == null) {
                        print("$L Content-Length: ${contentLength()}")
                    }
                }
            }
        }

        requestBody?.also {

            val bodyStarter = "$L Body:"
            print(bodyStarter)

            if (bodyHasUnknownEncoding(request.headers) ||
                    requestBody.isDuplex() ||
                    requestBody.isOneShot()) {
                print(BODY_OMITTED)
            } else {
                val buffer = Buffer()
                requestBody.writeTo(buffer)

                val contentType = requestBody.contentType()
                val charset: Charset = contentType?.charset(UTF8) ?: UTF8

                print("$L ${buffer.readString(charset)}")
                print(FOOTER)
            }
        } ?: print(FOOTER)

    }
    private fun logHeader(key:String, value:String) {
        print("$L ${key}: $value")
    }


    /**
     * 通过 [.logger] 打印字符串
     *
     * @param msg 要打印的字符串
     */
    private fun print(msg: String) {
        Log.println(logPriority.toInt(), tag, msg)
        moreAction.invoke(msg)
    }


    /**
     * 判断 Headers 是不是编码过的
     *
     * @param headers [Headers]
     * @return `true ` 编码过的
     */
    private fun isEncoded(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"]
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")

        //一行字符最大数量
        private const val MAX_LENGTH = 1024
        const val MAX_LINE_LENGTH = 300
        val LT = "┏"
        val FOOTER = "┗[END]━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        val LB = "┗"
        val BODY_OMITTED =   "┗[END]Body Omitted━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        val L = "┃"
        val CLINE = "━"
    }

}

internal fun String.appendLine(): String {
    if(length >= LoggingInterceptor.MAX_LINE_LENGTH) {
        return this
    }

    val sb = StringBuilder(this)
    repeat(LoggingInterceptor.MAX_LINE_LENGTH - length) {
        sb.append(LoggingInterceptor.CLINE)
    }
    return sb.toString()
}