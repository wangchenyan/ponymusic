package me.wcy.ponymusic.utils;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

/**
 * Json封装
 * Created by wcy on 2015/12/20.
 */
public abstract class JsonCallback<T> extends Callback<T> {
    private Class<T> mClass;

    public JsonCallback(Class<T> clazz) {
        this.mClass = clazz;
    }

    @Override
    public T parseNetworkResponse(Response response) throws IOException {
        String jsonString = response.body().string();
        return JSON.parseObject(jsonString, mClass);
    }
}
