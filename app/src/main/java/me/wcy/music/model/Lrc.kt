package me.wcy.music.model;

import com.google.gson.annotations.SerializedName;

/**
 * JavaBean
 * Created by hzwangchenyan on 2016/1/13.
 */
public class Lrc {
    @SerializedName("lrcContent")
    private String lrcContent;

    public String getLrcContent() {
        return lrcContent;
    }

    public void setLrcContent(String lrcContent) {
        this.lrcContent = lrcContent;
    }
}
