package me.wcy.music.model;

import android.graphics.Bitmap;

import me.wcy.music.enums.MusicTypeEnum;

/**
 * 单曲信息
 * Created by wcy on 2015/11/27.
 */
public class Music {
    // 歌曲类型 本地，网络
    private MusicTypeEnum type;
    // 歌曲id[本地歌曲]
    private long id;
    // 音乐标题
    private String title;
    // 艺术家
    private String artist;
    // 专辑
    private String album;
    // 持续时间
    private long duration;
    // 音乐路径
    private String uri;
    // 专辑封面路径[本地歌曲]
    private String coverUri;
    // 文件名
    private String fileName;
    // 专辑封面bitmap[网络歌曲]
    private Bitmap cover;

    public MusicTypeEnum getType() {
        return type;
    }

    public void setType(MusicTypeEnum type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(String coverUri) {
        this.coverUri = coverUri;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    /**
     * 对比本地歌曲是否相同
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Music)) {
            return false;
        }
        return this.getId() == ((Music) o).getId();
    }
}
