package com.example.streetreport;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;

public class HttpHandler {
    public static final String BASE_URL = "http://52.78.1.190:3000/";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType XML = MediaType.parse("application/xml; charset=utf-8");
    public static final int CONNECT_TIMEOUT = 15;
    public static final int WRITE_TIMEOUT = 15;
    public static final int READ_TIMEOUT = 15;
    private static HttpHandler instance;
    private static OkHttpClient client;

    public HttpHandler() {
        client = (new Builder()).connectTimeout(15L, TimeUnit.SECONDS).writeTimeout(15L, TimeUnit.SECONDS).readTimeout(15L, TimeUnit.SECONDS).build();
    }

    public static HttpHandler getInstance() {
        if (instance == null) {
            instance = new HttpHandler();
        }

        return instance;
    }

    public String GET(String url) throws IOException {
        Request request = (new okhttp3.Request.Builder()).url(url).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
