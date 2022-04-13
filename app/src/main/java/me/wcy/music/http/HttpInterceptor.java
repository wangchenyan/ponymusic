package me.wcy.music.http;

import android.os.Build;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hzwangchenyan on 2017/3/30.
 */
public class HttpInterceptor implements Interceptor {
    private static final String UA = "User-Agent";
    private static final String AUTH = "Authorization";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request;
        if(getToken().equals("")){
            request = chain.request()
                    .newBuilder()
                    .addHeader(UA, makeUA())
                    .build();
        } else {
            request = chain.request()
                    .newBuilder()
                    .addHeader(UA, makeUA())
                    .addHeader(AUTH,getToken())
                    .build();
        }

        return chain.proceed(request);
    }
    //add header to login
    private String getToken(){//暂时时固定的token，应该：1.已登录，从本地获取token。2.未登录，不加token返回空字符串""
        return "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmZWlmZWlmZWkiLCJleHAiOjE2NTA1NTQ5OTB9.ags8KVNNnHJKSM8YRyBIWRvLT2LcBz4NxcF33c-oYPxaCw9CTaZFtltgAL9sf4LDMvcBJMXE1jY7BFITIXJ1gg";
    }
    private String makeUA() {
        return Build.BRAND + "/" + Build.MODEL + "/" + Build.VERSION.RELEASE;
    }
}
