package io.github.wrobezin.eunha.crawler;

import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/23 1:03
 */
@Component
public class HttpClient {
    private final static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public final Response get(String url) throws IOException {
        return HTTP_CLIENT.newCall(new Request.Builder()
                .url(url)
                .get()
                .build()).execute();
    }

    public final Response post(String url, Map<String, String> formParam) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        formParam.forEach(formBuilder::add);
        return HTTP_CLIENT.newCall(new Request.Builder()
                .url(url)
                .post(formBuilder.build())
                .build()).execute();
    }

    public final Response postJson(String url, String json) throws IOException {
        return HTTP_CLIENT.newCall(new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build()).execute();
    }
}
