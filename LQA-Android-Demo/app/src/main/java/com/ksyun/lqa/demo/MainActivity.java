package com.ksyun.lqa.demo;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.ksyun.lqa.demo.skin.ui.DefaultDialog;
import com.ksyun.lqa.demo.skin.ui.MessageDialog;
import com.ksyun.lqa.demo.skin.util.DefaultOnClick;
import com.ksyun.lqa.demo.skin.util.ToastManager;
import com.ksyun.lqa.demo.test.TestRequestConfig;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {

    private Button mJoinLiveBtn;
    private ImageButton mHelpButton;
    private Dialog mDialog;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        mHandler = new Handler();
    }

    private void initView() {
        mJoinLiveBtn = findViewById(R.id.btn_join_live);
        mJoinLiveBtn.setOnClickListener(mOnClick);
        mHelpButton = findViewById(R.id.imgb_help);
        mHelpButton.setOnClickListener(mOnClick);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private DefaultOnClick mOnClick = new DefaultOnClick() {
        @Override
        protected void onViewClick(View view) {
            if (view.getId() == R.id.btn_join_live) {
                //获取配置信息,注意，这个接口特别的不稳定，只能测试使用
//                LivePlayerActivity.startActivity(MainActivity.this);
                ToastManager.showToast(MainActivity.this, "正在请求配置信息");
                view.setClickable(false);
                new TestRequestConfig().requestConfig(MainActivity.this, callback);
            } else if (view.getId() == R.id.imgb_help) {
                mDialog = new MessageDialog.Builder(MainActivity.this, true)
                        .setCloseListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                mDialog = null;
                            }
                        })
                        .show();
            }
        }
    };


    private Callback<ResponseBody> callback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            mJoinLiveBtn.setClickable(true);
            try {
                String str = response.body().string();
                JSONObject object = new JSONObject(str);
                JSONObject data = object.getJSONObject("data");
                QAConfig.mIMkey = data.getString("rcAppKey");
                QAConfig.mIMToken = data.getString("rcToken");
                QAConfig.mChatRoom = data.getString("messageRoom");
                QAConfig.mQARoom = data.getString("questionRoom");
                QAConfig.mServerUserId = data.getString("imUser");
                QAConfig.mContestSequenceId = data.getString("liveId");
                QAConfig.mMaxExtraLiveUsedInContest = data.getInt("maxExtraLives");
                QAConfig.mUserExtraLiveCount = data.getInt("lives");
                QAConfig.mKsyunKey = data.getString("kscAppKey");
                QAConfig.mLiveUrl = data.getString("liveUrl");
                QAConfig.mSignalingTimestamp = data.getString("X-KSC-SignalingTimestamp");
                QAConfig.mRequestSignaling = data.getString("X-KSC-RequestSignaling");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LivePlayerActivity.startActivity(MainActivity.this);
                        ToastManager.showToast(MainActivity.this, "配置信息请求成功了，您可以开始直播了");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastManager.showToast(MainActivity.this, "测试环境配置信息请求失败");
                    }
                });
            }
        }

        @Override
        public void onFailure(Call call, final Throwable t) {
            mJoinLiveBtn.setClickable(true);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToastManager.showToast(MainActivity.this, "测试环境配置信息请求失败:" + t.getMessage());
                }
            });
        }
    };
}
