# 波尼音乐
![](https://raw.githubusercontent.com/wangchenyan/PonyMusic/master/app/src/main/res/drawable-xxhdpi/ic_launcher.png)

## 系列文章
- [Android开源在线音乐播放器——波尼音乐](http://www.jianshu.com/p/1c0f5c4f64fa)
- [Android开源音乐播放器之播放器基本功能](http://www.jianshu.com/p/bc2f779a5400)
- [Android开源音乐播放器之高仿云音乐黑胶唱片](http://www.jianshu.com/p/f1d8eb8bb3e5)
- [Android开源音乐播放器之自动滚动歌词](http://www.jianshu.com/p/0feb6171b0c5)
- [Android开源音乐播放器之在线音乐列表自动加载更多](http://www.jianshu.com/p/576564627c96)

## 前言
毕业设计做的项目，答辩完了，就共享出来。

- 项目地址：https://github.com/wangchenyan/PonyMusic
- 有问题请提Issues
- 如果喜欢，欢迎Star！

## 简介
波尼音乐是一款开源Android在线音乐播放器。
- 播放本地音乐与在线音乐
- 在线音乐排行榜，如热歌榜、新歌榜等
- 高仿云音乐的黑胶唱片专辑封面
- 歌词显示，自动搜索歌词
- 夜间模式
- 定时关闭

## 更新说明
`v 1.2.3`
- 新增通知栏播放控制
- 修复魅族手机扫描不到音乐的问题
- 修复已知bug

`v 1.2`
- 修复在线音乐无法加载的问题
- 修复弱网时播放网络歌曲导致ANR的问题
- 修复每日启动图片无法更新的问题
- 下载在线歌曲可以显示专辑封面了
- 修复已知bug

`v 1.1`
- 支持 Android 6.0 运行时权限
- 修复已知bug

`v 1.0`
- First Release

## 下载地址
fir.im：http://fir.im/ponymusic

## TODO
- 在线音乐可以免下载加入我的音乐列表
- 在线音乐自动缓存
- 编辑音乐信息

## 项目
### 公开API
- 在线音乐：[百度音乐](http://mrasong.com/a/baidu-mp3-api-full)
- 天气数据：[高德地图](http://lbs.amap.com/)

### 开源技术
- [okhttp-utils](https://github.com/hongyangAndroid/okhttp-utils)
- [Glide](https://github.com/bumptech/glide)

### 关键代码
黑胶唱片专辑封面绘制流程
```java
@Override
protected void onDraw(Canvas canvas) {
    // 1.绘制顶部虚线
    mTopLine.setBounds(0, 0, getWidth(), mTopLineHeight);
    mTopLine.draw(canvas);
    // 2.绘制黑胶唱片外侧半透明边框
    mCoverBorder.setBounds(mDiscPoint.x - mCoverBorderWidth, mDiscPoint.y - mCoverBorderWidth,
            mDiscPoint.x + mDiscBitmap.getWidth() + mCoverBorderWidth, mDiscPoint.y +
                    mDiscBitmap.getHeight() + mCoverBorderWidth);
    mCoverBorder.draw(canvas);
    // 3.绘制黑胶
    // 设置旋转中心和旋转角度，setRotate和preTranslate顺序很重要
    mDiscMatrix.setRotate(mDiscRotation, mDiscCenterPoint.x, mDiscCenterPoint.y);
    // 设置图片起始坐标
    mDiscMatrix.preTranslate(mDiscPoint.x, mDiscPoint.y);
    canvas.drawBitmap(mDiscBitmap, mDiscMatrix, null);
    // 4.绘制封面
    mCoverMatrix.setRotate(mDiscRotation, mCoverCenterPoint.x, mCoverCenterPoint.y);
    mCoverMatrix.preTranslate(mCoverPoint.x, mCoverPoint.y);
    canvas.drawBitmap(mCoverBitmap, mCoverMatrix, null);
    // 5.绘制指针
    mNeedleMatrix.setRotate(mNeedleRotation, mNeedleCenterPoint.x, mNeedleCenterPoint.y);
    mNeedleMatrix.preTranslate(mNeedlePoint.x, mNeedlePoint.y);
    canvas.drawBitmap(mNeedleBitmap, mNeedleMatrix, null);
}
```
歌词绘制流程
```java
@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    // 中心Y坐标
    float centerY = getHeight() / 2 + mTextSize / 2 + mAnimOffset;

    // 无歌词文件
    if (!hasLrc()) {
        float centerX = (getWidth() - mCurrentPaint.measureText(label)) / 2;
        canvas.drawText(label, centerX, centerY, mCurrentPaint);
        return;
    }

    // 画当前行
    String currStr = mLrcTexts.get(mCurrentLine);
    float currX = (getWidth() - mCurrentPaint.measureText(currStr)) / 2;
    canvas.drawText(currStr, currX, centerY, mCurrentPaint);

    // 画当前行上面的
    for (int i = mCurrentLine - 1; i >= 0; i--) {
        String upStr = mLrcTexts.get(i);
        float upX = (getWidth() - mNormalPaint.measureText(upStr)) / 2;
        float upY = centerY - (mTextSize + mDividerHeight) * (mCurrentLine - i);
        // 超出屏幕停止绘制
        if (upY - mTextSize < 0) {
            break;
        }
        canvas.drawText(upStr, upX, upY, mNormalPaint);
    }

    // 画当前行下面的
    for (int i = mCurrentLine + 1; i < mLrcTimes.size(); i++) {
        String downStr = mLrcTexts.get(i);
        float downX = (getWidth() - mNormalPaint.measureText(downStr)) / 2;
        float downY = centerY + (mTextSize + mDividerHeight) * (i - mCurrentLine);
        // 超出屏幕停止绘制
        if (downY > getHeight()) {
            break;
        }
        canvas.drawText(downStr, downX, downY, mNormalPaint);
    }
}
```

## 截图
![](https://raw.githubusercontent.com/wangchenyan/PonyMusic/master/art/screenshot_01.jpg)
![](https://raw.githubusercontent.com/wangchenyan/PonyMusic/master/art/screenshot_02.jpg)
![](https://raw.githubusercontent.com/wangchenyan/PonyMusic/master/art/screenshot_03.jpg)
![](https://raw.githubusercontent.com/wangchenyan/PonyMusic/master/art/screenshot_04.jpg)
![](https://raw.githubusercontent.com/wangchenyan/PonyMusic/master/art/screenshot_05.jpg)
![](https://raw.githubusercontent.com/wangchenyan/PonyMusic/master/art/screenshot_06.jpg)

## 关于作者
简书：http://www.jianshu.com/users/3231579893ac<br>
微博：http://weibo.com/wangchenyan1993

## License

    Copyright 2016 wangchenyan

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
