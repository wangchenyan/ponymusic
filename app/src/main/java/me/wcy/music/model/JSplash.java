package me.wcy.music.model;

/**
 * 启动画面Java bean
 * Created by hzwangchenyan on 2016/3/2.
 */
public class JSplash {
    private String text;
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
