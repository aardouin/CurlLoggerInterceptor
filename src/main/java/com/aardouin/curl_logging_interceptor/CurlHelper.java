package com.aardouin.curl_logging_interceptor;


import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

public class CurlHelper {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public static String buildCurlString(Request request) {

        RequestBody requestBody = request.body();
        StringBuilder curl = new StringBuilder("curl -i \\\n");

        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            curl.append("-H \"");
            curl.append(headers.name(i));
            curl.append(":");
            curl.append(headers.value(i));
            curl.append("\" \\\n");

        }
        try {
            if (requestBody != null) {
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
        }catch (IOException ex){
            ex.printStackTrace();

        }

        curl.append("-X ").append(request.method()).append(" \\\n");
        curl.append("\"").append(request.url().toString()).append("\"");
        return curl.toString();
    }
}

