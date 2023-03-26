package com.dijia478.wxgzh_chat.utils;

import okhttp3.*;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author dijia478
 */
public class HttpUtil {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 5003)))
            .retryOnConnectionFailure(true)
            .connectionPool(new ConnectionPool(200, 5, TimeUnit.MINUTES))
            .connectTimeout(600L, TimeUnit.SECONDS)
            .readTimeout(600L, TimeUnit.SECONDS)
            .build();


    public static String doPostJson(String url, String data, Map<String, String> headerMap) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, data);
        String[] objects = headerMap.keySet().toArray(new String[0]);
        Request request = new Request
                .Builder()
                .header(objects[0], headerMap.get(objects[0]))
                .header(objects[1], headerMap.get(objects[1]))
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}



