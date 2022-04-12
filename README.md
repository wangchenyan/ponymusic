# TO DO LIST
（团队成员可自行添加）
### unaughty:
    1. 修改在线音乐接口，获取后端音频进行播放，实现安卓端和后端基本对接；
    2. 增加运动音乐功能；

# CONTRIBUTION
（记录团队成员对本项目 *安卓端* 的贡献）

# 项目文件引导

## activity
1. BaseActivity: 基类
2. AboutActivity： 加载关于 APP 的项目信息
3. MusicInfoActivity： 加载歌曲信息
4. OnlineMusicActivity
5. SettingActivity： 功能设置活动

## adapater
1. PlaylistAdapter: 本地音乐列表适配器

## application
1. AppCache： 缓存本地音乐列表、在线歌单列表、活动栈等等

## constants
定义常量

## enums
定义枚举类型变量

## executer
1. PlayMusic： 播放音乐执行器（基类）
2. PlayOnlineMusic： 播放在线音乐
3. WeatherExecutor： 请求天气信息

## fragment
1. BaseFragment： 基类，下面的 fragment 均基于此拓展。对工具类 2. ViewBinder，RxBus，PermissionReq 进行初始化
3. PlayFragment： 正在播放界面
4. LocalMusicFragment： 本地音乐界面
5. SheetListFragment： 在线音乐界面

## Http
1. HttpClient: 使用 okhttp3 进行启动界面获取，文件下载，在线歌曲列表，LRC 歌词文件等等网络信息服务
2. JsonCallback： 解析网络相应为 JSON 格式

## loader
1. MusicLoaderCallback

## model（类似于数据库中的表）
1. Music： 音乐模型，为 AudioPlayer 最终播放对象
2. OnlineMusic： 在线音乐模型
3. OnlineMusicList： 在线音乐列表模型
4. SheetInfo： 歌单信息模型
5. SearchMusic： 搜索结果的歌曲列表模型
6. ArtistInfo： 歌手模型（星座，体重，身高，国籍，URL，简介，姓名，头像，生日）
7. DownloadInfo： 下载信息模型
8. Lrc： 歌词模型
9. Splash： 启动界面模型

## service
1. AudioPlayer： 音频播放服务，对原生 API MediaPlayer 的拓展
2. AudioFocusManager： 音频焦点处理服务，通知、来电、其他播放器抢占会造成音频焦点丢失，可能需要重新获取焦点

## storage
1. db: 
   1. DBManager 数据库管理器
2. preference:
   1. Preferences: 封装原生 PreferenceManager 类 ，设计一系列配置项（以键值对方式存储） 

## utlis（工具类）
1. blinding：@Bind 快速绑定 XML 对象
2. MusicUtils: 通过 ContentProvider 配合 Media 相关类查询系统数据库，获得媒体库中的歌曲信息(扫描本地音乐)

## Widget
1. AlbumCoverView: 绘制高仿云音乐的黑胶唱片专辑封面

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
3. ~~天气 API 无法使用~~
解决方法： https://github.com/wangchenyan/ponymusic/issues/27
获取 SHA1 值：https://blog.csdn.net/qq_29269233/article/details/53725865?spm=1001.2101.3001.6650.2&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7ERate-2.pc_relevant_default&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7ERate-2.pc_relevant_default&utm_relevant_index=5

```cmd
D:
cd D:\Android S\jre\bin
keytool.exe -list -v -keystore D:\study\oppo\ponymusic\app\debug.keystore
android
```
4. 无法将音乐批量添加进播放列表

