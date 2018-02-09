package com.ksyun.lqa.demo.skin;

import android.os.Handler;

import com.ksyun.live.qa.common.utils.KLog;
import com.ksyun.lqa.demo.QAConfig;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.ksyun.media.player.KSYTextureView;

import java.io.IOException;

/**
 * @Author: [xiaoqiang]
 * @Description: [LivePlayerUI]
 * @CreateDate: [2018/1/11]
 * @UpdateDate: [2018/1/11]
 * @UpdateUser: [xiaoqiang]
 * @UpdateRemark: []
 */

public class LivePlayerUI {
    private String TAG = LivePlayerUI.class.getName();
    private KSYTextureView mVideoView;
    private Handler mHandler;

    public LivePlayerUI() {
    }

    public void initPlayer(KSYTextureView mTextureView) {
        //不需要再显示的创建mVideoView
//        mVideoView = (KSYTextureView) findViewById(R.id.ksy_textureview);
        mVideoView = mTextureView;
        //设置监听器
        mVideoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnPreparedListener(mOnPreparedListener);
        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangeListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
        mVideoView.setBufferTimeMax(1);
        //设置播放参数
        mVideoView.setTimeout(5, 30);
        try {
            mVideoView.setDataSource(QAConfig.mLiveUrl);
            mVideoView.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mHandler = new Handler();
    }

    public void onPausePlayer() {
        if (mVideoView != null) {
            //mAudioBackgroundPlay为true表示切换到后台后仍然播放音频
            mVideoView.runInBackground(true);
        }
    }

    public void onResumePlayer() {
        if (mVideoView != null) {
            mVideoView.runInForeground();
        }
    }

    public void releasePlayer() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        if (mVideoView != null) {
            //释放播放器
            mVideoView.release();
        }
    }

    public KSYMediaPlayer getPlayer() {
        if (mVideoView != null) {
            return mVideoView.getMediaPlayer();
        }
        return null;
    }


    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            if (mVideoView != null) {
                // 设置视频伸缩模式，此模式为裁剪模式
                mVideoView.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                // 开始播放视频
                mVideoView.start();
            }
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener =
            new IMediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                    KLog.d(TAG, "播放器缓存更新:" + percent);
                }
            };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener =
            new IMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IMediaPlayer mp) {
                    KLog.d(TAG, "视频播放完成");
                }
            };

    public IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            KLog.d(TAG, "MediaPlayer info,i= " + i + ",i1:" + i1);
            if (i == IMediaPlayer.MEDIA_INFO_SUGGEST_RELOAD) {
                mVideoView.reload(QAConfig.mLiveUrl, true);
            }
            return false;
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangeListener
            = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
            KLog.d(TAG, "MediaPlayer onVideoSizeChanged,width= " + width + ",height:" + height);
        }
    };


    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mVideoView.reload(QAConfig.mLiveUrl, true);
        }
    };
    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            KLog.e(TAG, "MediaPlayer mOnErrorListener,what= " + what + ",extra:" + extra);
            switch (what) {
                case IMediaPlayer.MEDIA_ERROR_IO:
                case IMediaPlayer.MEDIA_ERROR_CONNECT_SERVER_FAILED:
                case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                case IMediaPlayer.MEDIA_INFO_UNKNOWN:
                    mHandler.removeCallbacks(mRunnable);
                    mHandler.postDelayed(mRunnable, 1000);
                    break;
            }

            return false;
        }
    };
}
