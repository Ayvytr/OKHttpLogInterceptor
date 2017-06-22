package com.ayvytr.okhttplogginginterceptor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;

/**
 * <a href='https://github.com/square/okhttp'></a> 拦截器，提供了打印状态，头，响应体等5种类型的log拦截打印方式.
 *
 * @author Ayvytr <a href="https://github.com/Ayvytr" target="_blank">'s GitHub</a>
 * @since 1.0.0
 */
public final class LoggingInterceptor implements Interceptor
{
    private final Charset UTF8 = Charset.forName("UTF-8");
    private final Logger logger;
    private volatile HttpLoggingLevel level = HttpLoggingLevel.NONE;

    public LoggingInterceptor()
    {
        this(HttpLoggingLevel.BODY);
    }

    public LoggingInterceptor(HttpLoggingLevel level)
    {
        this(level, Logger.DEFAULT);
    }

    public LoggingInterceptor(HttpLoggingLevel level, Logger logger)
    {
        this.level = level;
        if(this.level == null)
        {
            this.level = HttpLoggingLevel.BODY;
        }
        this.logger = logger;
    }

    /**
     * 更改拦截等级
     *
     * @param level 新的拦截等级
     * @return {@link LoggingInterceptor}
     */
    public LoggingInterceptor setLevel(HttpLoggingLevel level)
    {
        if(level == null)
        {
            this.level = HttpLoggingLevel.BODY;
        }
        else
        {
            this.level = level;
        }

        return this;
    }

    /**
     * 返回拦截等级
     *
     * @return {@link #level}
     */
    public HttpLoggingLevel getLevel()
    {
        return level;
    }

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        return logIntercept(chain);
    }

    /**
     * 拦截日志方法.
     *
     * @param chain {@link Chain}
     * @return {@link Response}
     * @throws IOException
     */
    private Response logIntercept(Chain chain) throws IOException
    {
        Request request = chain.request();

        if(level == HttpLoggingLevel.NONE)
        {
            return chain.proceed(request);
        }

        long startNs = System.nanoTime();
        Response response;
        try
        {
            response = chain.proceed(request);
        } catch(IOException e)
        {
            logger.log("┗━━━ HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        printState(request, response, tookMs, chain);

        switch(level)
        {
            //do nothing
//            case STATE:
//                break;
            case HEADERS:
                printHeaders(request, response);
                break;
            case BODY:
                printResponseBody(response);
                break;
            case ALL:
                printFull(request, response);
                break;
        }

        printEnd();

        return response;
    }

    private void printEnd()
    {
        logger.log("┗━━━ END HTTP");
    }

    /**
     * 打印Http请求状态.
     *
     * @param request  {@link Request}
     * @param response {@link Response}
     * @param tookMs   请求花费时长
     * @param chain    {@link okhttp3.Interceptor.Chain}
     */
    private void printState(Request request, Response response, long tookMs, Chain chain)
    {
        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage = String.format(Locale.getDefault(),
                "┏━━━ [%s %d %s][%s %dms] %s",
                request.method(),
                response.code(),
                response.message(),
                protocol,
                tookMs,
                request.url());

        logger.log(requestStartMessage);
    }


    /**
     * 打印所有请求状态
     *
     * @param request  {@link Request}
     * @param response {@link Response}
     * @throws IOException
     */
    private void printFull(Request request, Response response) throws IOException
    {
        printResponseBody(response);
        printHeaders(request, response);
    }


    /**
     * 打印请求头和响应头
     *
     * @param request  {@link Request}
     * @param response {@link Response}
     * @throws IOException
     */
    private void printHeaders(Request request, Response response) throws IOException
    {
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        if(hasRequestBody)
        {
            logger.log("Content-Length: " + requestBody.contentLength());
            MediaType mediaType = requestBody.contentType();
            // Request body headers are only present when installed as a network interceptor. Force
            // them to be included (when available) so there values are known.
            if(mediaType != null)
            {
                logger.log("Content-Type: " + mediaType);
            }

            Headers headers = request.headers();
            for(int i = 0, count = headers.size(); i < count; i++)
            {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if(!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name))
                {
                    logger.log(name + ": " + headers.value(i));
                }
            }
        }

        Headers headers = response.headers();
        if(headers != null)
        {
            for(int i = 0, count = headers.size(); i < count; i++)
            {
                logger.log(headers.name(i) + ": " + headers.value(i));
            }
        }
    }

    /**
     * 打印响应体
     *
     * @param response {@link Response}
     * @throws IOException
     */
    private void printResponseBody(Response response) throws IOException
    {
        if(HttpHeaders.hasBody(response))
        {
            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if(contentType != null)
            {
                charset = contentType.charset(UTF8);
            }

            logger.log(buffer.clone().readString(charset));
        }
    }

    /**
     * log打印等级，默认提供了 {@link #DEFAULT}，{@link #WARN} 两种等级，差别在Android Logcat中可以体现出来
     */
    public interface Logger
    {
        void log(String message);

        /**
         * A {@link Logger} defaults output appropriate for the current platform.
         */
        Logger DEFAULT = new Logger()
        {
            @Override
            public void log(String message)
            {
                Platform.get().log(Platform.INFO, message, null);
            }
        };

        /**
         * A {@link Logger} warn level output appropriate for the current platform.
         */
        Logger WARN = new Logger()
        {
            @Override
            public void log(String message)
            {
                Platform.get().log(Platform.WARN, message, null);
            }
        };
    }
}
