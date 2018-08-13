package com.aardouin.curl_logging_interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Platform;

/**
 * Created by Alexis Ardouin on 20/06/16.
 */

public class CurlLoggingInterceptor implements Interceptor {


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

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        logger.log(CurlHelper.buildCurlString(request));
        return chain.proceed(request);
    }
}
