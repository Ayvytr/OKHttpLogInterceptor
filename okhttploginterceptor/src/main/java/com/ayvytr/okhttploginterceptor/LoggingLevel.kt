package com.ayvytr.okhttploginterceptor

/**
 * [LoggingInterceptor] 中使用到的log打印等级，包含7个log类型，有 [.NONE], [.URL_BODY], [.SINGLE],
 * [.STATE], [.HEADERS], [.BODY], [.ALL]，类型丰富，按类型打印了log，可以设置为你喜欢的等级，直接使用.
 *
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 1.0.0
 */
enum class LoggingLevel {
    /**
     * 不打印log
     */
    NONE,

    /**
     * 打印url和响应体.
     *
     * 例子：
     * <pre>`┣━ http://xx.com 响应体
    `</pre> *
     */
    URL_BODY,

    /**
     * 打印Http状态，url, 请求时长和响应体.
     *
     * 例子：
     * <pre>`┣━ [GET 200 OK][http/1.1 915ms] http://wthrcdn.etouch.cn/weather_mini?city=%E6%B7%B1%E5%9C%B3 响应体
    `</pre> *
     */
    SINGLE,

    /**
     * 打印Http状态和url
     *
     * 例子:
     * <pre>`┣━ [GET 200 OK][http/1.1 168ms] http://wthrcdn.etouch.cn/weather_mini?city=%E6%B7%B1%E5%9C%B3
    `</pre> *
     */
    STATE,

    /**
     * 打印Http状态，url，请求头和响应头
     *
     * 例子:
     * <pre>`--> POST /greeting http/1.1
     * Host: example.com
     * Content-Type: plain/text
     * Content-Length: 3
     * --> END POST
     *
     * <-- 200 OK (22ms)
     * Content-Type: plain/text
     * Content-Length: 6
     * <-- END HTTP
    `</pre> *
     */
    HEADERS,

    /**
     * 打印Http状态，url, 请求时长和响应体.
     *
     * 例子：
     * <pre>`┏━━ [GET 200 OK][http/1.1 915ms] http://xx.com
     * 响应体
     * ┗━ END HTTP
    `</pre> *
     */
    BODY,

    /**
     * 打印http状态，url，请求时长，响应体，请求头和响应头
     *
     * 例子:
     * <pre>`┏━ [GET 200 OK][http/1.1 52ms] http://wthrcdn.etouch.cn/weather_mini?city=%E6%B7%B1%E5%9C%B3
     * 响应体
     * Date: Thu, 22 Jun 2017 06:07:27 GMT
     * Cache-Control: must-revalidate, max-age=300
     * Connection: Keep-Alive
     * Server: Tengine/2.1.2
     * Access-Control-Allow-Origin: *
     * Access-Control-Allow-Headers: *
     * Access-Control-Allow-Methods: *
     * Fw-Cache-Status: hit
     * Fw-Via: DISK HIT from 115.231.47.62, DISK HIT from 163.177.175.65
     * ┗━ END HTTP
    `</pre> *
     */
    ALL
}