# TO DO LIST
（团队成员可自行添加）
### unaughty:
    1. 修改在线音乐接口，获取后端音频进行播放，实现安卓端和后端基本对接；
    2. 增加运动音乐功能；

# CONTRIBUTION
（记录团队成员对本项目 *安卓端* 的贡献）

# 项目文件引导

## activity
BaseActivity: 基类
AboutActivity： 加载关于 APP 的项目信息
MusicInfoActivity： 加载歌曲信息
OnlineMusicActivity

## adapater
PlaylistAdapter: 本地音乐列表适配器

## application
AppCache： 缓存本地音乐列表、在线歌单列表、活动栈等等

## constants
定义常量

## enums
定义枚举类型变量

## executer
PlayMusic： 播放音乐执行器（基类）
PlayOnlineMusic： 播放在线音乐

## fragment
BaseFragment： 基类，下面的 fragment 均基于此拓展。对工具类 ViewBinder，RxBus，PermissionReq 进行初始化
PlayFragment： 正在播放界面
LocalMusicFragment： 本地音乐界面
SheetListFragment： 在线音乐界面

## Http
HttpClient: 使用 okhttp3 进行启动界面获取，文件下载，在线歌曲列表，LRC 歌词文件等等网络信息服务
JsonCallback： 解析网络相应为 JSON 格式

## loader
MusicLoaderCallback

## model（类似于数据库中的表）
Music： 音乐模型，为 AudioPlayer 最终播放对象
OnlineMusic： 在线音乐模型
OnlineMusicList： 在线音乐列表模型
SheetInfo： 歌单信息模型
SearchMusic： 搜索结果的歌曲列表模型
ArtistInfo： 歌手模型（星座，体重，身高，国籍，URL，简介，姓名，头像，生日）
DownloadInfo： 下载信息模型
Lrc： 歌词模型
Splash： 启动界面模型

## service
AudioPlayer： 音频播放服务
AudioFocusManager： 音频焦点处理服务，通知、来电、其他播放器抢占会造成音频焦点丢失，可能需要重新获取焦点

## storage
db: DBManager 数据库管理器

## utlis（工具类）
blinding：@Bind 快速绑定 XML 对象
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
2. 切换“夜间模式”后，本地歌曲列表无法加载
3. 天气 API 无法使用

