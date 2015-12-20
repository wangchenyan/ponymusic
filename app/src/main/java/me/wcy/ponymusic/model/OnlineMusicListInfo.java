package me.wcy.ponymusic.model;

import java.io.Serializable;

/**
 * 歌单信息
 * Created by wcy on 2015/12/20.
 */
public class OnlineMusicListInfo implements Serializable {
    private String title;
    private int icon;
    /**
     * 1、新歌榜，2、热歌榜
     * 11、摇滚榜，12、爵士，16、流行
     * 21、欧美金曲榜，22、经典老歌榜，23、情歌对唱榜，24、影视金曲榜，25、网络歌曲榜
     */
    private String type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
