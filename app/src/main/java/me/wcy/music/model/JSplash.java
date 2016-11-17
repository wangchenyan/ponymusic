package me.wcy.music.model;

import com.google.gson.annotations.SerializedName;

/**
 * 启动画面Java bean
 * Created by hzwangchenyan on 2016/3/2.
 */
public class JSplash {
    @SerializedName("text")
    private String text;
    @SerializedName("img")
    private String img;

    public void setText(String text) {
        this.text = text;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getText() {
        return text;
    }

    public String getImg() {
        return img;
    }
}
