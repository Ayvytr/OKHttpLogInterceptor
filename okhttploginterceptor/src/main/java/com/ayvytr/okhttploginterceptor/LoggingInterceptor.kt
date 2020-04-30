package com.ayvytr.okhttploginterceptor

import android.util.Log
import okhttp3.*
import okio.Buffer
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
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

    private val UTF8 = Charset.forName("UTF-8")

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
        val startNs = System.nanoTime()

        if (logType == LogType.NONE || !showLog) {
            return chain.proceed(request)
        }

        val connection = chain.connection()

        printRequest(request, connection)

        val response: Response
        response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            print(String.format("┣━━━ [HTTP EXCEPTION] url:%s %s", request.url, e.message))
            throw e
        }

        printResponse(response)
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val httpHeaderString = getHttpHeaderString(request, response, tookMs, chain)
        when (logType) {
            LogType.LEAST -> printSingle(request, response, httpHeaderString)
            LogType.ALL   -> printAll(request, response, httpHeaderString)
        }
        return response
    }

    private fun printRequest(request: Request, connection: Connection?) {
        val requestBody = request.body
        val isAll = logType == LogType.ALL

        val header = "${LT}[${request.method}]${LINE}"
        print(header)

        val headers = request.headers
        if (isAll) {
            for (i in 0 until headers.size) {
                logHeader(headers, i)
            }

        }

        requestBody?.also {
            // Request body headers are only present when installed as a network interceptor. When not
            // already present, force them to be included (if available) so their values are known.
//            requestBody.contentType()?.let {
//                if (headers["Content-Type"] == null) {
//                    print("Content-Type: $it")
//                }
//            }
//            if (requestBody.contentLength() != -1L) {
//                if (headers["Content-Length"] == null) {
//                    print("Content-Length: ${requestBody.contentLength()}")
//                }
//            }

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
                val charset= UTF8

                print(buffer.readString(charset))
//                logger.log("--> END ${request.method} (${requestBody.contentLength()}-byte body)")
                print(FOOTER)
            }
        }

    }

    private fun logHeader(headers: Headers, i: Int) {
        val value = headers.value(i)
        print(headers.name(i) + ": " + value)
    }

    /**
     * 打印内容过长文本，超过一行长度，折行显示
     *
     * @param text 要打印的文本
     */
    private fun printLong(text: String) {
        val length = text.length
        if (length <= MAX_LENGTH) {
            print(text)
        } else {
            var lineNum = length / MAX_LENGTH
            if (length % MAX_LENGTH != 0) {
                lineNum++
            }
            for (i in 1..lineNum) {
                if (i < lineNum) {
                    print(text.substring((i - 1) * MAX_LENGTH, i * MAX_LENGTH))
                } else {
                    print(text.substring((i - 1) * MAX_LENGTH, length))
                }
            }
        }
    }

    /**
     * 打印 [logType.HEADERS] 类型的log
     *
     * @param request          [Request]
     * @param response         [Response]
     * @param httpHeaderString http头字符串
     */
    @Throws(IOException::class)
    private fun printHeaders(request: Request, response: Response,
                             httpHeaderString: String) {
        print(httpHeaderString)
        printHttpHeaders(request, response)
        printEnd()
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
     * 打印 [logType.BODY] 类型的log
     *
     * @param response         [Response]
     * @param httpHeaderString http头字符串
     */
    @Throws(IOException::class)
    private fun printBody(request: Request, response: Response,
                          httpHeaderString: String) {
        print(httpHeaderString)
        printLong(getResponseBody(request, response))
        printEnd()
    }

    /**
     * 打印 [logType.SINGLE] 类型的log
     *
     * @param request          [Request]
     * @param response         [Response]
     * @param httpHeaderString http头字符串
     */
    @Throws(IOException::class)
    private fun printSingle(request: Request, response: Response,
                            httpHeaderString: String) {
        val responseBody = getResponseBody(request, response)
        val format = String.format("%s %s", httpHeaderString, responseBody)
        if (format.length > MAX_LENGTH) {
            print(httpHeaderString)
            printLong(responseBody)
        } else {
            print(format)
        }
    }

    /**
     * 打印 [logType.STATE] 类型的log
     *
     * @param httpHeaderString http头字符串
     */
    private fun printState(httpHeaderString: String) {
        print(httpHeaderString)
    }

    /**
     * 打印log尾.
     */
    private fun printEnd() {
        print("┗━ END HTTP")
    }

    /**
     * 返回Http请求状态字符串，见 [.getHttpStateString]
     *
     * @param request  [Request]
     * @param response [Response]
     * @param tookMs   请求花费时长
     * @param chain    [Chain]
     * @return 请求状态字符串.
     */
    private fun getHttpHeaderString(request: Request, response: Response,
                                    tookMs: Long,
                                    chain: Interceptor.Chain): String {
        return String.format("%s %s",
                             headerSymbol,
                             getHttpStateString(request, response, tookMs, chain))
    }

    /**
     * 返回请求状态字符串，包括 Http method, response code, response message, protocol, 请求花费时长，url.
     *
     * @param request  [Request]
     * @param response [Response]
     * @param tookMs   请求花费时长
     * @param chain    [okhttp3.Interceptor.Chain]
     * @return 请求状态字符串.
     */
    private fun getHttpStateString(request: Request, response: Response,
                                   tookMs: Long,
                                   chain: Interceptor.Chain): String {
        val connection = chain.connection()
        val protocol = connection?.protocol() ?: Protocol.HTTP_1_1
        return String.format(Locale.getDefault(),
                             "[%s %d %s][%s %dms] %s",
                             request.method,
                             response.code,
                             response.message,
                             protocol,
                             tookMs,
                             request.url)
    }

    /**
     * 返回log头符号
     *
     * @return log头符号
     */
    private val headerSymbol: String
        private get() = if (logType == LogType.LEAST || logType == LogType.ALL) "┣━" else "┏━"

    /**
     * 打印所有请求状态
     *
     * @param request          [Request]
     * @param response         [Response]
     * @param httpHeaderString http头字符串
     */
    @Throws(IOException::class)
    private fun printAll(request: Request, response: Response,
                         httpHeaderString: String) {
        print(httpHeaderString)
        printLong(getResponseBody(request, response))
        printHttpHeaders(request, response)
        printEnd()
    }

    /**
     * 打印请求头和响应头，如果有的话.
     *
     * @param request  [Request]
     * @param response [Response]
     */
    @Throws(IOException::class)
    private fun printHttpHeaders(request: Request,
                                 response: Response) {
        val requestBody = request.body
        val hasRequestBody = requestBody != null
        if (hasRequestBody) {
            print("Content-Length: " + requestBody!!.contentLength())
            val mediaType = requestBody.contentType()
            // Request body headers are only present when installed as a network interceptor. Force
            // them to be included (when available) so there values are known.
            if (mediaType != null) {
                print("Content-Type: $mediaType")
            }
            val headers = request.headers
            var i = 0
            val count = headers.size
            while (i < count) {
                val name = headers.name(i)
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equals(name, ignoreCase = true) && !"Content-Length".equals(name, ignoreCase = true)) {
                    print(name + ": " + headers.value(i))
                }
                i++
            }
        }
        val headers = response.headers
        if (headers != null) {
            var i = 0
            val count = headers.size
            while (i < count) {
                print(headers.name(i) + ": " + headers.value(i))
                i++
            }
        }
    }

    /**
     * 返回响应体字符串
     *
     * @param request  [Request]
     * @param response [Response]
     * @return 响应体字符串
     */
    @Throws(IOException::class)
    private fun getResponseBody(request: Request,
                                response: Response): String {
        var body = "[No Response Body]"
        if (true) {
            val responseBody = response.body
            val source = responseBody!!.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer()
            if (isEncoded(request.headers)) {
                body = "[Body: Encoded]"
            } else if (!isPlaintext(buffer)) {
                val url = request.url.toString()
                body = if (!url.contains("?")) {
                    String.format("[File:%s]", url.substring(url.lastIndexOf("/") + 1))
                } else {
                    "[Body: Not readable]"
                }
            } else {
                var charset = UTF8
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }
                body = buffer.clone().readString(charset)
            }
        }
        return body
    }


    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private fun isPlaintext(buffer: Buffer): Boolean {
        return try {
            val prefix = Buffer()
            val byteCount = if (buffer.size < 64) buffer.size else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (e: EOFException) {
            false // Truncated UTF-8 sequence.
        }
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
        //一行字符最大数量
        private const val MAX_LENGTH = 1024
        val LT = "┏"
        val FOOTER = "┗[END]━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        val LB = "┗"
        val BODY_OMITTED =   "┗[END]Body Omitted━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        val L = "┃"
        val LINE = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    }

}