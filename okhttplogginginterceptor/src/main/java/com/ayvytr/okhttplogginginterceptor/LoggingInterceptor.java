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
    private volatile LoggingLevel level = LoggingLevel.NONE;

    public LoggingInterceptor()
    {
        this(LoggingLevel.SINGLE);
    }

    public LoggingInterceptor(LoggingLevel level)
    {
        this(level, Logger.DEFAULT);
    }

    public LoggingInterceptor(LoggingLevel level, Logger logger)
    {
        this.level = level;
        if(this.level == null)
        {
            this.level = LoggingLevel.SINGLE;
        }
        this.logger = logger;
    }

    /**
     * 更改log等级，见 {@link LoggingLevel}.
     *
     * @param level 新的拦截等级
     * @return {@link LoggingInterceptor}
     */
    public LoggingInterceptor setLevel(LoggingLevel level)
    {
        this.level = level;
        if(this.level == null)
        {
            this.level = LoggingLevel.BODY;
        }

        return this;
    }

    /**
     * 返回拦截等级
     *
     * @return {@link #level}
     */
    public LoggingLevel getLevel()
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

        long startNs = System.nanoTime();
        Response response;
        try
        {
            response = chain.proceed(request);
        } catch(IOException e)
        {
            print(String.format("┣━━━ [HTTP FAILED] url:%s exception:%s", request.url(), e.getMessage()));
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        String httpHeaderString = getHttpHeaderString(request, response, tookMs, chain);

        switch(level)
        {
            //do nothing
//            case NONE:
//                break;
            case URL_BODY:
                printUrlBody(request, response);
                break;
            case SINGLE:
                printSingle(response, httpHeaderString);
                break;
            case STATE:
                printState(httpHeaderString);
                break;
            case HEADERS:
                printHeaders(request, response, httpHeaderString);
                break;
            case BODY:
                printBody(response, httpHeaderString);
                break;
            case ALL:
                printAll(request, response, httpHeaderString);
                break;
        }

        return response;
    }


    /**
     * 打印 {@link LoggingLevel#URL_BODY} 类型的log
     *
     * @param request  {@link Request}
     * @param response {@link Response}
     */
    private void printUrlBody(Request request, Response response) throws IOException
    {
        print(String.format("%s %s %s", getHeaderSymbol(), request.url(), getResponseBody(response)));
    }


    /**
     * 打印 {@link LoggingLevel#HEADERS} 类型的log
     *
     * @param request          {@link Request}
     * @param response         {@link Response}
     * @param httpHeaderString http头字符串
     */
    private void printHeaders(Request request, Response response, String httpHeaderString) throws IOException
    {
        print(httpHeaderString);
        printHttpHeaders(request, response);
        printEnd();
    }


    /**
     * 通过 {@link #logger} 打印字符串 s
     *
     * @param s 要打印的字符串
     */
    private void print(String s)
    {
        logger.log(s);
    }


    /**
     * 打印 {@link LoggingLevel#BODY} 类型的log
     *
     * @param response         {@link Response}
     * @param httpHeaderString http头字符串
     */
    private void printBody(Response response, String httpHeaderString) throws IOException
    {
        print(httpHeaderString);
        print(getResponseBody(response));
        printEnd();
    }

    /**
     * 打印 {@link LoggingLevel#SINGLE} 类型的log
     *
     * @param response         {@link Response}
     * @param httpHeaderString http头字符串
     */
    private void printSingle(Response response, String httpHeaderString) throws IOException
    {
        print(String.format("%s %s", httpHeaderString, getResponseBody(response)));
    }

    /**
     * 打印 {@link LoggingLevel#STATE} 类型的log
     *
     * @param headerString http头字符串
     */
    private void printState(String headerString)
    {
        print(headerString);
        printEnd();
    }

    /**
     * 打印log尾.
     */
    private void printEnd()
    {
        print("┗━ END HTTP");
    }

    /**
     * 返回Http请求状态字符串，见 {@link #getHttpStateString(Request, Response, long, Chain)}
     *
     * @param request  {@link Request}
     * @param response {@link Response}
     * @param tookMs   请求花费时长
     * @param chain    {@link Chain}
     * @return 请求状态字符串.
     */
    private String getHttpHeaderString(Request request, Response response, long tookMs, Chain chain)
    {
        return String.format("%s %s",
                getHeaderSymbol(),
                getHttpStateString(request, response, tookMs, chain));
    }

    /**
     * 返回请求状态字符串，包括 Http method, response code, response message, protocol, 请求花费时长，url.
     *
     * @param request  {@link Request}
     * @param response {@link Response}
     * @param tookMs   请求花费时长
     * @param chain    {@link okhttp3.Interceptor.Chain}
     * @return 请求状态字符串.
     */
    private String getHttpStateString(Request request, Response response, long tookMs, Chain chain)
    {
        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        return String.format(Locale.getDefault(),
                "[%s %d %s][%s %dms] %s",
                request.method(),
                response.code(),
                response.message(),
                protocol,
                tookMs,
                request.url());
    }


    /**
     * 返回log头符号
     *
     * @return log头符号
     */
    private String getHeaderSymbol()
    {
        return (level == LoggingLevel.SINGLE || level == LoggingLevel.URL_BODY) ? "┣━" : "┏━";
    }


    /**
     * 打印所有请求状态
     *
     * @param request          {@link Request}
     * @param response         {@link Response}
     * @param httpHeaderString http头字符串
     */
    private void printAll(Request request, Response response, String httpHeaderString) throws IOException
    {
        print(httpHeaderString);
        print(getResponseBody(response));
        printHttpHeaders(request, response);
        printEnd();
    }


    /**
     * 打印请求头和响应头，如果有的话.
     *
     * @param request  {@link Request}
     * @param response {@link Response}
     */
    private void printHttpHeaders(Request request, Response response) throws IOException
    {
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        if(hasRequestBody)
        {
            print("Content-Length: " + requestBody.contentLength());
            MediaType mediaType = requestBody.contentType();
            // Request body headers are only present when installed as a network interceptor. Force
            // them to be included (when available) so there values are known.
            if(mediaType != null)
            {
                print("Content-Type: " + mediaType);
            }

            Headers headers = request.headers();
            for(int i = 0, count = headers.size(); i < count; i++)
            {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if(!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name))
                {
                    print(name + ": " + headers.value(i));
                }
            }
        }

        Headers headers = response.headers();
        if(headers != null)
        {
            for(int i = 0, count = headers.size(); i < count; i++)
            {
                print(headers.name(i) + ": " + headers.value(i));
            }
        }
    }


    /**
     * 返回响应体字符串
     *
     * @param response {@link Response}
     * @return 响应体字符串
     */
    private String getResponseBody(Response response) throws IOException
    {
        String body = "";
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

            body = buffer.clone().readString(charset);
        }

        return body;
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
