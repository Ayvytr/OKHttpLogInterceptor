package com.ayvytr.okhttplogginginterceptor;

/**
 * Created by Do on 2017/6/21.
 */
public enum LoggingLevel
{
    /**
     * No logs.
     */
    NONE,
    URL_BODY,
    SINGLE,
    /**
     * Logs request and response lines.
     * <p>
     * <p>Example:
     * <pre>{@code
     * --> POST /greeting http/1.1 (3-byte body)
     *
     * <-- 200 OK (22ms, 6-byte body)
     * }</pre>
     */
    STATE,
    /**
     * Logs request and response lines and their respective headers.
     * <p>
     * <p>Example:
     * <pre>{@code
     * --> POST /greeting http/1.1
     * Host: example.com
     * Content-Type: plain/text
     * Content-Length: 3
     * --> END POST
     *
     * <-- 200 OK (22ms)
     * Content-Type: plain/text
     * Content-Length: 6
     * <-- END HTTP
     * }</pre>
     */
    HEADERS,
    /**
     *
     */
    BODY,
    /**
     * Logs request and response lines and their respective headers and bodies (if present).
     * <p>
     * <p>Example:
     * <pre>{@code
     * --> POST /greeting http/1.1
     * Host: example.com
     * Content-Type: plain/text
     * Content-Length: 3
     *
     * Hi?
     * --> END POST
     *
     * <-- 200 OK (22ms)
     * Content-Type: plain/text
     * Content-Length: 6
     *
     * Hello!
     * <-- END HTTP
     * }</pre>
     */
    ALL
}
