# TO DO LIST
（团队成员可自行添加）
### unaughty:
    1. 修改在线音乐接口，获取后端音频进行播放，实现安卓端和后端基本对接；
    2. 增加运动音乐功能；

# CONTRIBUTION
（记录团队成员对本项目 *安卓端* 的贡献）

# 项目文件引导

## activity
AboutActivity： 加载关于 APP 的项目信息
MusicInfoActivity： 加载歌曲信息

## fragment
PlayFragment： 正在播放界面

## Http
HttpClient: 使用 okhttp3 进行启动界面获取，文件下载，在线歌曲列表，LRC 歌词文件等等网络信息服务
JsonCallback： 解析网络相应为 JSON 格式

## model
Music

## service
AudioPlayer： 音频播放服务

## utlis（工具类）
MusicUtils: 通过 ContentProvider 配合 Media 相关类查询系统数据库，获得媒体库中的歌曲信息(扫描本地音乐)

## Widget
AlbumCoverView: 绘制高仿云音乐的黑胶唱片专辑封面

# TO BE FIXED
1. PlayFragment中歌词视图无法转回专辑视图
```java
    private void initCoverLrc() {
        mAlbumCoverView.initNeedle(AudioPlayer.get().isPlaying());
        mAlbumCoverView.setOnClickListener(v -> switchCoverLrc(false));
        mLrcView.setDraggable(true, this);
        mLrcView.setOnTapListener((view, x, y) -> switchCoverLrc(true));    // to be fixed
        initVolume();
        switchCoverLrc(true);
    }
```

