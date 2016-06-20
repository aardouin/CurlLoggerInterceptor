package com.aardouin.curl_logging_interceptor;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Platform;
import okhttp3.internal.http.HttpEngine;
import okio.Buffer;
import okio.BufferedSource;
import timber.log.Timber;

/**
 * Created by WOPATA on 20/06/16.
 */

public class CurlLoggingInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    public interface Logger {
        void log(String message);

        /**
         * A {@link Logger} defaults output appropriate for the current platform.
         */
        Logger DEFAULT = new Logger() {
            @Override
            public void log(String message) {
                Platform.get().log(message);
            }
        };
    }

    public CurlLoggingInterceptor() {
        this(Logger.DEFAULT);
    }

    public CurlLoggingInterceptor(Logger logger) {
        this.logger = logger;
    }

    private final Logger logger;

    /**
     * Change the level at which this interceptor logs.
     */
    public CurlLoggingInterceptor setLevel(Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        return this;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();


        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        StringBuilder curl = new StringBuilder("curl -i \\\n");

        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            curl.append("-H \"");
            curl.append(headers.name(i));
            curl.append(":");
            curl.append(headers.value(i));
            curl.append("\" \\\n");

        }
        if (hasRequestBody) {
            curl.append("-d \"");
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            String body = buffer.readString(charset);
            curl.append(StringEscapeUtils.escapeJava(body));
            curl.append("\" \\\n");
        }

        curl.append("-X ").append(request.method()).append(" \\\n");
        curl.append("\"").append(request.url().toString()).append("\"");

        Timber.d(curl.toString());
        return chain.proceed(request);
    }
}
