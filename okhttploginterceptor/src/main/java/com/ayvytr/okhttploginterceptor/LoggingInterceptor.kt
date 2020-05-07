package com.ayvytr.okhttploginterceptor

import android.util.Log
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * [](https://github.com/square/okhttp) 拦截器，提供了打印状态，头，响应体等5种类型的log拦截打印方式.
 *
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 1.0.0
 */
class LoggingInterceptor @JvmOverloads constructor(var showLog: Boolean = true,
                                                   var isShowAll: Boolean = false,
                                                   var tag: String = "OkHttp",
                                                   var priority: Priority = Priority.V,
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
            val starter = "${LT}[Response][${request.method}] ${request.url} ".appendLine()
            print(starter)
            print("$L Exception:$e")
            print(FOOTER)
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        printResponse(response, tookMs)
        return response
    }

    private fun printResponse(response: Response, tookMs: Long) {
        val request = response.request
        val responseBody = response.body

        val starter = "${LT}[Response][${request.method} ${response.code} ${response.message} ${tookMs}ms] ${request.url} ".appendLine()
        print(starter)

        val headers = response.headers
        if (isShowAll) {
            logHeader("Protocol", response.protocol.toString())

            headers.forEach {
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
            val peekBody = response.peekBody(Long.MAX_VALUE)
            if (isShowAll && responseBody.contentLength() == -1L) {
                print("$L Content-Length: ${peekBody.contentLength()}")
            }

            val bodyStarter = "$L Body:"
            print(bodyStarter)

            peekBody.formatAsPossible(MAX_LENGTH).forEach {
                print("$L $it")
            }
        }

        print(FOOTER)
    }

    private fun printRequest(request: Request) {
        val requestBody = request.body

        val header = "${LT}[Request][${request.method}] ${request.url} ".appendLine()
        print(header)

        val headers = request.headers
        if (isShowAll) {
            val querySize = request.url.querySize
            if (querySize > 0) {
                print("$L Query Parameters: ${request.url.query}")
            }

            headers.forEach {
                logHeader(it.first, it.second)
            }

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
            if (bodyHasUnknownEncoding(request.headers) ||
                    requestBody.isDuplex() ||
                    requestBody.isOneShot()) {
                print(BODY_OMITTED)
            } else {
                val bodyStarter = "$L Body:"
                print(bodyStarter)

                requestBody.formatAsPossible().forEach {
                    print("$L $it")
                }

                print(FOOTER)
            }
        } ?: print(FOOTER)
    }

    private fun logHeader(key: String, value: String) {
        print("$L ${key}: $value")
    }


    /**
     * 通过 [.logger] 打印字符串
     *
     * @param msg 要打印的字符串
     */
    private fun print(msg: String) {
        Log.println(priority.toInt(), tag, msg)
        moreAction.invoke(msg)
    }


    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }

    companion object {

        //一行字符最大数量
        private const val MAX_LENGTH = 1024
        const val MAX_LINE_LENGTH = 300
        val LT = "┏"
        val FOOTER = "┗[END]━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        val LB = "┗"
        val BODY_OMITTED = "┗[END]Body Omitted━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        val L = "┃"
        val CLINE = "━"
    }

}
