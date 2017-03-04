package me.wcy.music.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Locale;

/**
 * 启动画面Java bean
 * Created by hzwangchenyan on 2016/3/2.
 */
public class Splash {
    private static final String URL = "http://cn.bing.com%s_720x1280.jpg";

    @SerializedName("images")
    private List<ImagesBean> images;

    public String getUrl() {
        if (images != null && !images.isEmpty()) {
            String baseUrl = images.get(0).urlbase;
            if (!TextUtils.isEmpty(baseUrl)) {
                return String.format(Locale.getDefault(), URL, baseUrl);
            }
        }
        return null;
    }

    private static class ImagesBean {
        @SerializedName("startdate")
        private String startdate;
        @SerializedName("fullstartdate")
        private String fullstartdate;
        @SerializedName("enddate")
        private String enddate;
        @SerializedName("url")
        private String url;
        @SerializedName("urlbase")
        private String urlbase;
        @SerializedName("copyright")
        private String copyright;
        @SerializedName("copyrightlink")
        private String copyrightlink;
        @SerializedName("quiz")
        private String quiz;
        @SerializedName("wp")
        private boolean wp;
        @SerializedName("hsh")
        private String hsh;
        @SerializedName("drk")
        private int drk;
        @SerializedName("top")
        private int top;
        @SerializedName("bot")
        private int bot;
        @SerializedName("hs")
        private List<?> hs;
    }
}
