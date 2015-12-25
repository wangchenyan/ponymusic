package me.wcy.ponymusic.model;

/**
 * JavaBean
 * Created by wcy on 2015/12/20.
 */
public class JOnlineMusicList {
    JOnlineMusic[] song_list;
    JOnlineMusicListInfo billboard;

    public JOnlineMusic[] getSong_list() {
        return song_list;
    }

    public void setSong_list(JOnlineMusic[] song_list) {
        this.song_list = song_list;
    }

    public JOnlineMusicListInfo getBillboard() {
        return billboard;
    }

    public void setBillboard(JOnlineMusicListInfo billboard) {
        this.billboard = billboard;
    }
}
