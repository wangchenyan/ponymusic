package me.wcy.music.executor;

/**
 * Created by hzwangchenyan on 2017/8/11.
 */
public class DownloadMusicInfo {
    private String title;
    private String musicPath;
    private String coverPath;

    public DownloadMusicInfo(String title, String musicPath, String coverPath) {
        this.title = title;
        this.musicPath = musicPath;
        this.coverPath = coverPath;
    }

    public String getTitle() {
        return title;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public String getCoverPath() {
        return coverPath;
    }
}
