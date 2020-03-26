package com.ayvytr.okhttploginterceptor

import okhttp3.*
import okhttp3.internal.platform.Platform
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
 */
class LoggingInterceptor @JvmOverloads constructor(level: LoggingLevel? = LoggingLevel.SINGLE,
                                                   logger: Logger = Logger.DEFAULT) :
    Interceptor {
    private val UTF8 = Charset.forName("UTF-8")
    private val logger: Logger

    /**
     * 返回拦截等级
     *
     * @return [.level]
     */
    @Volatile
    var level: LoggingLevel? = LoggingLevel.NONE
        private set

    /**
     * 更改log等级，见 [LoggingLevel].
     *
     * @param level 新的拦截等级
     * @return [LoggingInterceptor]
     */
    fun setLevel(level: LoggingLevel?): LoggingInterceptor {
        this.level = level
        if (this.level == null) {
            this.level = LoggingLevel.BODY
        }
        return this
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return logIntercept(chain)
    }

    /**
     * 拦截日志方法.
     *
     * @param chain [Chain]
     * @return [Response]
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun logIntercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startNs = System.nanoTime()
        val response: Response
        response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            print(String.format("┣━━━ [HTTP FAILED] url:%s exception:%s", request.url, e.message))
            throw e
        }
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val httpHeaderString = getHttpHeaderString(request, response, tookMs, chain)
        when (level) {
            LoggingLevel.URL_BODY -> printUrlBody(request, response)
            LoggingLevel.SINGLE   -> printSingle(request, response, httpHeaderString)
            LoggingLevel.STATE    -> printState(httpHeaderString)
            LoggingLevel.HEADERS  -> printHeaders(request, response, httpHeaderString)
            LoggingLevel.BODY     -> printBody(request, response, httpHeaderString)
            LoggingLevel.ALL      -> printAll(request, response, httpHeaderString)
        }
        return response
    }

    /**
     * 打印 [LoggingLevel.URL_BODY] 类型的log
     *
     * @param request  [Request]
     * @param response [Response]
     */
    @Throws(IOException::class)
    private fun printUrlBody(request: Request, response: Response) {
        val responseBody = getResponseBody(request, response)
        val format = String.format("%s %s %s", headerSymbol, request.url, responseBody)
        if (format.length > MAX_LENGTH) {
            print(String.format("%s %s", headerSymbol, request.url))
            printLong(responseBody)
            printEnd()
        } else {
            print(format)
        }
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
     * 打印 [LoggingLevel.HEADERS] 类型的log
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
     * 通过 [.logger] 打印字符串 s
     *
     * @param s 要打印的字符串
     */
    private fun print(s: String) {
        logger.log(s)
    }

    /**
     * 打印 [LoggingLevel.BODY] 类型的log
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
     * 打印 [LoggingLevel.SINGLE] 类型的log
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
     * 打印 [LoggingLevel.STATE] 类型的log
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
        private get() = if (level === LoggingLevel.SINGLE || level === LoggingLevel.URL_BODY || level === LoggingLevel.STATE) "┣━" else "┏━"

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
     * log打印等级，默认提供了 [.DEFAULT]，[.WARN] 两种等级，差别在Android Logcat中可以体现出来
     */
    interface Logger {
        fun log(message: String?)

        companion object {
            /**
             * A [Logger] defaults output appropriate for the current platform.
             */
            val DEFAULT: Logger = object :
                Logger {
                override fun log(message: String?) {
                    Platform.get().log(message ?: "null", Platform.INFO, null)
                }
            }

            /**
             * A [Logger] warn level output appropriate for the current platform.
             */
            val WARN: Logger = object :
                Logger {
                override fun log(message: String?) {
                    Platform.get().log(message ?: "null", Platform.WARN, null)
                }
            }
        }
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

    companion object {
        //一行字符最大数量
        private const val MAX_LENGTH = 1024
    }

    init {
        this.level = level
        if (this.level == null) {
            this.level = LoggingLevel.SINGLE
        }
        this.logger = logger
    }
}