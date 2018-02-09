# 直播问答
## 1 简述
直播问答SDK，提供实时的答题下发功能和聊天评论功能。

注意,直播问答SDK不提供播放功能，播放功能可以仿照demo上，使用KSYMediaPlayer_Android。

```
// 播放SDK地址
https://github.com/ksvc/KSYMediaPlayer_Android
```

## 2 接入流程
### 2.1 环境要求
* Android SDK Build-tools 请升级到 21 及以上版本。
* JAVA 编译版本 JDK 1.7 及以上版本。
* Android SDK 最低支持 Android API 15: Android 4.0.3。
### 2.2 导入AAR
* 问答SDK暂时仅提供aar文件，请仿照Demo接入LibLQA-release.aar
* SDK需要您使用okhttp3
* 如果您需要接入播放器，请依赖播放相关的工程libksyplayer


完整的配置如下：

```gradle
android {
    compileSdkVersion 27
    buildToolsVersion '27'
      sourceSets {
            main {
                jniLibs.srcDir 'libs'
            }
        }
    }
    repositories {
        flatDir {
            dirs 'libs'
        }
    }
   dependencies {
       compile fileTree(include: ['*.jar'], dir: 'libs')
       compile(name: 'LibLQA-release', ext: 'aar')
       compile 'com.android.support:support-compat:27.0.0'
       compile 'com.android.support:support-v4:27.0.0'
       compile 'com.ksyun.media:libksyplayer-java:2.1.2'
       compile 'com.ksyun.media:libksyplayer-armv7a:2.1.2'
       compile 'com.ksyun.media:libksyplayer-arm64:2.1.2'
       compile 'com.squareup.okhttp3:okhttp:3.9.0'
   }

```
### 2.3 使用SDK需要的参数
使用问答SDK时，需要配置一些必须的参数，这些参数都在LQAConfig中进行配置。
示例代码如LivePlayerActivity
```java
        LQAConfig mConfig = new LQAConfig();
        mConfig.setIMInfo(QAConfig.mIMkey, QAConfig.mIMToken);
        // 设置问答房间
        mConfig.setChatMessageId(QAConfig.mQARoom);
        // 设置Ksyun key
        mConfig.setKsyunAppKey(QAConfig.mKsyunKey);
        // 设置当前用户是否可参与答题环节，默认不可以
        mConfig.setUserContestStatus(true);
        // 设置问题下发用户
        mConfig.setServerUserId(QAConfig.mServerUserId);
        // 设置最大使用复活卡次数
        mConfig.setMaxExtraLiveUsedInContest(QAConfig.mMaxExtraLiveUsedInContest);
        // 设置当前用户有多少复活卡
        mConfig.setUserExtraLiveCount(QAConfig.mUserExtraLiveCount);
        // 设置当前用户id
        mConfig.setUserId(QAConfig.mUid);
        // 设置当前直播场次
        mConfig.setContestLiveId(QAConfig.mContestSequenceId);

```
这些参数，在服务端对接时，都需要您的APP Server下发给您。
### 2.4 API
1. 所有对外提供的接口走在LQAClient类中
2. Demo展示请参考LivePlayerActivity和QAActionUI
3. 详细API说明请参考WIKI描述
## 3 注意事项
1. Android Build-tools必须是21及以上，这个为了兼容Android系统8.0的问题
2. 如果您在项目中已经使用了融云SDK，在集成问答直播时，需要替换为使用问答直播
中集成的融云库。当前问答直播中使用的是：v2_8_26版本的融云
3. 在按照WIKI要求中对问答直播进行初始化时，请释放掉您开启的所有融云资源

## 4 反馈与建议
### 4.1 反馈模版
|类型|描述|
|:--:|:--:|
|SDK名称	|LQA_Android|
|SDK版本	|v1.0.0|
|设备型号	|oppo r9s|
|OS版本	|Android 6.0.1|
|问题描述	|描述问题出现的现象|
|操作描述	|描述经过如何操作出现上述问题|
|额外附件|文本形式控制台log、crash报告、其他辅助信息（界面截屏或录像等）|
### 4.2 短视频解决方案咨询
金山云官方产品客服，帮您快速了解对接金山云短视频解决方案：

 ![image](https://raw.githubusercontent.com/wiki/ksvc/KSVSShortVideoKit_Android/images/wechat.png)
### 4.3 联系方式
  * 主页：[金山云](http://www.ksyun.com/)
  * 邮箱: zengfanping@kingsoft.com
  * QQ讨论群：
    * 574179720 视频云技术交流群
    * 620036233 视频云Android技术交流
    * 以上两个加一个QQ群即可
  * Issues: https://github.com/ksvc/LQA_Android/issues