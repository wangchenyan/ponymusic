# 波尼音乐
![](https://raw.githubusercontent.com/wangchenyan/ponymusic/master/app/src/main/res/drawable-xxhdpi/ic_launcher.webp)

## 系列文章
- [重生！入门级开源音乐播放器APP —— 波尼音乐](https://juejin.cn/post/7294072229003952143)
- [Android开源在线音乐播放器——波尼音乐](https://juejin.im/post/5c373a32e51d4551cc6df6db)
- [Android开源音乐播放器之播放器基本功能](https://juejin.im/post/5c373a32e51d45521315fc50)
- [Android开源音乐播放器之高仿云音乐黑胶唱片](https://juejin.im/post/5c373a336fb9a04a016488e8)
- [Android开源音乐播放器之自动滚动歌词](https://juejin.im/post/5c373a336fb9a049f43b85de)
- [Android开源音乐播放器之在线音乐列表自动加载更多](https://juejin.im/post/5c373a336fb9a049b82aaaaf)

## 展示
### 视频
[![](https://raw.githubusercontent.com/wangchenyan/ponymusic/master/art/video_cover.jpg)](https://www.ixigua.com/7294169212384182291)

### 截图
![](https://raw.githubusercontent.com/wangchenyan/ponymusic/master/art/screenshot.jpg)

## 功能
> 后续可能会根据大家的反馈增加或调整功能

### 本地功能
- 基于 Media3 + ExoPlayer 构建播放能力
- 添加和播放本地音乐文件
- 专辑封面显示
- 歌词显示，支持拖动歌词调节播放进度
- 通知栏控制
- 夜间模式
- 定时关闭

### 在线功能
- 登录网易云（验证码+扫码）
- 同步网易云歌单
- 每日推荐
- 歌单广场
- 排行榜
- 搜索歌曲和歌单
- 添加歌曲到歌单
- 喜欢歌曲
- 下载歌曲
- 管理歌单
- 设置在线播放/下载音质

## 体验
> 欢迎大家体验，如果发现功能问题或兼容性问题，可以提 [GitHub Issue](https://github.com/wangchenyan/ponymusic/issues)

### 环境要求
- Android 手机（系统版本为 Android 5.0 及以上）
- 电脑（非必须）

### 安装步骤
1. 搭建网易云服务器<br>
   由于我们使用的是非官方 API，因此需要自行搭建 API 服务器。<br>
   clone [NeteaseCloudMusicApi](https://github.com/Binaryify/NeteaseCloudMusicApi) 服务端项目到本地，根据项目说明安装并运行服务，需要确认电脑和手机处于同一局域网
2. 安装 APP<br>
   点击下载[最新安装包](https://github.com/wangchenyan/ponymusic/releases)
3. 设置域名<br>
   打开 APP，点击左上角汉堡按钮，打开抽屉，点击「域名设置」，输入步骤1中的地址（包含端口）
4. 设置完成即可体验

### 没有电脑，如何体验？
> 其实有一些同仁已经将网易云 API 服务部署到公网了，我们可以直接用🐶。
>
> 这里不方便直接贴地址，下面教大家如何找到可以用的服务：
>
> 用 Google 搜索「[网易云音乐API](https://www.google.com.hk/search?q=%E7%BD%91%E6%98%93%E4%BA%91%E9%9F%B3%E4%B9%90API)」，点击搜索结果链接，如果页面打开后是下图这样（注意：非作者的 github.io 页面），恭喜！你找到了可以直接使用的服务，拷贝地址栏链接，输入到步骤3即可。
>
> 如果设置域名后 APP 接口报错，说明这个域名不可用，可以尝试其他结果。
>
> ![](https://raw.githubusercontent.com/wangchenyan/ponymusic/master/art/api_page.jpg)

## 更新说明
`2.3.0`
- 播放器内核升级为 Media3 + ExoPlayer
- 修复歌单内歌曲超过1000首加载失败的问题

`2.2.0`
- 增加添加歌曲到歌单
- 播放页增加喜欢歌曲和下载歌曲
- 设置页增加设置下载音质
- 支持删除歌单中的歌曲
- 优化大屏播放页效果

`2.1.0`
- 增加手机验证码登录（需要使用最新版本服务端代码）
- 支持设置在线播放音质
- 增加 Banner 实现
- 增加接口缓存，非首次打开加载更快
- 适配 Android 14
- 修复部分设备后台启动 Service 崩溃

`2.0.0`
- 使用 Kotlin 重写
- 接口改为网易云音乐
- 增加「每日推荐」、「歌单广场」、「排行榜」、「搜索」等在线功能
- 适配到 Android 13

`1.3.0`
- 新增歌词支持上下拖动
- 新增支持分屏模式
- 新增本地歌曲支持按大小和时长过滤
- 新增下载的歌曲文件自动添加专辑封面
- 新增编辑歌曲信息
- 新增5.0以上系统支持联动系统媒体中心，锁屏显示播放信息
- 修复已知bug

`1.2.3`
- 新增通知栏播放控制
- 修复魅族手机扫描不到音乐的问题
- 修复已知bug

`1.2.0`
- 修复在线音乐无法加载的问题
- 修复弱网时播放网络歌曲导致ANR的问题
- 修复每日启动图片无法更新的问题
- 下载在线歌曲可以显示专辑封面了
- 修复已知bug

`1.1.0`
- 支持 Android 6.0 运行时权限
- 修复已知bug

`1.0.0`
- First Release

## TODO
- [ ] 桌面小部件 by [Glance](https://developer.android.com/jetpack/compose/glance/create-app-widget)
- [x] 适配 Android 14
- [x] 在线音乐可以免下载加入我的音乐列表
- [ ] 在线音乐自动缓存
- [x] 编辑音乐信息

## Liked it?
如果你觉得该项目对你有帮助，欢迎给它一个 star⭐️
[![Stargazers over time](https://starchart.cc/wangchenyan/ponymusic.svg?variant=adaptive)](https://starchart.cc/wangchenyan/ponymusic)

## 依赖
> 站在巨人的肩膀上

### 在线服务
- 在线音乐: [Binaryify/NeteaseCloudMusicApi: 网易云音乐 Node.js API service](https://github.com/Binaryify/NeteaseCloudMusicApi)

### 开源技术
- 播放器：Media3 + ExoPlayer
- 页面: MVVM
- 网络: [Retrofit](https://square.github.io/retrofit/)
- 数据库: [Room](https://developer.android.com/jetpack/androidx/releases/room)
- 依赖注入: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- 图片: [Glide](https://github.com/bumptech/glide)
- 统计&崩溃收集: [Firebase](https://firebase.google.com)
- 路由框架: [wangchenyan/crouter: 支持组件化的 Android 路由框架](https://github.com/wangchenyan/crouter)
- 歌词控件: [wangchenyan/lrcview: Android beautiful draggable lyric view library](https://github.com/wangchenyan/lrcview)
- 启动任务: [wangchenyan/init: Android 启动任务调度](https://github.com/wangchenyan/init)
- 通用库: [wangchenyan/android-common: 个人使用的 Android 通用库](https://github.com/wangchenyan/android-common)
- RecyclerView Adapter: [wangchenyan/radapter3: A multitype adapter for Android recyclerview](https://github.com/wangchenyan/radapter3)

## 关于作者
掘金: https://juejin.im/user/2313028193754168<br>
微博: https://weibo.com/wangchenyan1993

## License

    Copyright 2024 wangchenyan

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
