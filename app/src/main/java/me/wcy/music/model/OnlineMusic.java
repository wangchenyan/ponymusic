package me.wcy.music.model;

import com.google.gson.annotations.SerializedName;

/**
 * JavaBean
 * Created by wcy on 2015/12/20.
 */
public class OnlineMusic {
    @SerializedName("audio_url")
    private String audioUrl;
//    @SerializedName("pic_big")
    @SerializedName("cover_url")//目前后端没有加入此功能
    private String pic_big;
//    @SerializedName("pic_small")
    private String pic_small=pic_big;
    @SerializedName("lrclink")
    private String lrclink;
//    @SerializedName("song_id")
    @SerializedName("id")
    private String song_id;
//    @SerializedName("title")
    @SerializedName("name")
    private String title;
    @SerializedName("ting_uid")
    private String ting_uid;
//    @SerializedName("album_title")
    @SerializedName("album")
    private String album_title;
//    @SerializedName("artist_name")
    @SerializedName("artist")
    private String artist_name;

    public String getPic_big() {
        return pic_big;
    }

    public void setPic_big(String pic_big) {
        this.pic_big = pic_big;
    }

    public String getPic_small() {
        return pic_small;
    }

    public void setPic_small(String pic_small) {
        this.pic_small = pic_small;
    }

    public String getLrclink() {
        return lrclink;
    }

    public void setLrclink(String lrclink) {
        this.lrclink = lrclink;
    }

    public String getAudioUrl() { return audioUrl;}
    public void setAudioUrl(String audioUrl) { this.audioUrl=audioUrl; }

    public String getSong_id() {
        return song_id;
    }

    public void setSong_id(String song_id) {
        this.song_id = song_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTing_uid() {
        return ting_uid;
    }

    public void setTing_uid(String ting_uid) {
        this.ting_uid = ting_uid;
    }

    public String getAlbum_title() {
        return album_title;
    }

    public void setAlbum_title(String album_title) {
        this.album_title = album_title;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }
}
