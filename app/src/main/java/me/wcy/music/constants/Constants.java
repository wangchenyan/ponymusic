package me.wcy.music.constants;

/**
 * 常量
 * Created by wcy on 2015/11/28.
 */
public interface Constants {
    String FILENAME_MP3 = ".mp3";
    String FILENAME_LRC = ".lrc";
    int MUSIC_LIST_SIZE = 20;
    String BASE_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting";
    String METHOD_GET_MUSIC_LIST = "baidu.ting.billboard.billList";
    String METHOD_DOWNLOAD_MUSIC = "baidu.ting.song.play";
    String METHOD_ARTIST_INFO = "baidu.ting.artist.getInfo";
    String METHOD_SEARCH_MUSIC = "baidu.ting.search.catalogSug";
    String METHOD_LRC = "baidu.ting.song.lry";
    String PARAM_METHOD = "method";
    String PARAM_TYPE = "type";
    String PARAM_SIZE = "size";
    String PARAM_OFFSET = "offset";
    String PARAM_SONG_ID = "songid";
    String PARAM_TING_UID = "tinguid";
    String PARAM_QUERY = "query";
    String SPLASH_URL = "http://news-at.zhihu.com/api/4/start-image/720*1184";
}
