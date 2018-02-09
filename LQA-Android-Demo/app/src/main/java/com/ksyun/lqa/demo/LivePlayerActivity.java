package com.ksyun.lqa.demo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ksyun.live.qa.LQAClient;
import com.ksyun.live.qa.LQAConfig;
import com.ksyun.live.qa.common.utils.KLog;
import com.ksyun.lqa.demo.skin.LivePlayerUI;
import com.ksyun.lqa.demo.skin.QAActionUI;
import com.ksyun.lqa.demo.skin.chat.ChatParse;
import com.ksyun.lqa.demo.skin.chat.LiveChatListAdapter;
import com.ksyun.lqa.demo.skin.chat.NemoMessageEntity;
import com.ksyun.lqa.demo.skin.ui.DefaultDialog;
import com.ksyun.lqa.demo.skin.ui.MessageDialog;
import com.ksyun.lqa.demo.skin.util.DefaultOnClick;
import com.ksyun.lqa.demo.skin.util.ToastManager;
import com.ksyun.media.player.KSYTextureView;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * @Author: [xiaoqiang]
 * @Description: [LivePlayerActivity]
 * @CreateDate: [2018/1/9]
 * @UpdateDate: [2018/1/9]
 * @UpdateUser: [xiaoqiang]
 * @UpdateRemark: []
 */

public class LivePlayerActivity extends FragmentActivity {

    private final static String TAG = LiveChatListAdapter.class.getName();
    private LiveChatListAdapter mListAdapter;
    private RelativeLayout mRLSeedComment;
    private EditText mCommentEdit;
    private Button mSeedComment;
    private ListView mPlayerMsgList;
    private ImageButton mSendChat;
    private ImageView mHelpImage;
    private ImageButton mBackBtn;
    private LQAClient mClient;
    private LivePlayerUI mPlayer;
    private QAActionUI mQA;
    private LQAConfig mConfig;
    private ImageView mEliminate;
    private Dialog mDialog;
    private KSYTextureView mTextureView;
    private RelativeLayout mRootLayout;
    private boolean mIsShowSoftKey;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LivePlayerActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_live);
        hideStatus();
        initView();
        mPlayer = new LivePlayerUI();
        mPlayer.initPlayer(mTextureView);
        initQAClient();
        ToastManager.showToast(this, "当前版本号:" + mClient.getVersion());
    }

    private void initView() {
        mPlayerMsgList = findViewById(R.id.list_live_chat);
        mSendChat = findViewById(R.id.imgb_chat);
        mSendChat.setOnClickListener(mOnClick);
        mListAdapter = new LiveChatListAdapter(this, mPlayerMsgList);
        mPlayerMsgList.setAdapter(mListAdapter);
        mRLSeedComment = findViewById(R.id.rl_seed_comment);
        mCommentEdit = findViewById(R.id.edit_comment);
        mSeedComment = findViewById(R.id.btn_seed_comment);
        mSeedComment.setOnClickListener(mOnClick);
        mHelpImage = findViewById(R.id.imgb_help);
        mHelpImage.setOnClickListener(mOnClick);
        mBackBtn = findViewById(R.id.imgb_back);
        mBackBtn.setOnClickListener(mOnClick);
        mEliminate = findViewById(R.id.imgv_eliminate);
        mTextureView = findViewById(R.id.ksy_textureview);
        mRootLayout = findViewById(R.id.root);
        mRootLayout.setOnTouchListener(mOnTouchListener);
        mPlayerMsgList.setOnTouchListener(mOnTouchListener);
    }

    private void initQAClient() {
        mConfig = new LQAConfig();
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
        // 设置播放器对象
        mConfig.setMediaPlayer(mPlayer.getPlayer());
        // 设置播放地址
        mConfig.setPlayerUrl(QAConfig.mLiveUrl);

        mClient = new LQAClient(mConfig);
        mQA = new QAActionUI(LivePlayerActivity.this, mClient, mConfig);
        mClient.init(LivePlayerActivity.this, mEventListener);
        mClient.setOnLiveChatMessageListener(mChatMessageListener);
        mClient.setOnLQAQuestionListener(mQA);
        mClient.setOnLQAResultListener(mQA);
        mClient.setOnLQAMatchResultListener(mQA);
        mQA.setOnAnswerStatusListener(
                new QAActionUI.OnAnswerStatusListener() {
                    @Override
                    public void onAnswer(boolean isAnswer) {
                        if (!isAnswer) {
                            mEliminate.setVisibility(View.VISIBLE);
                        } else {
                            mEliminate.setVisibility(View.GONE);
                        }
                    }
                });

        mQA.setOnDialogChangeListener(new QAActionUI.OnDialogChangeListener() {
            @Override
            public void showDialog(Dialog dialog) {
                hideSoftInput();
            }

            @Override
            public void hideDialog(Dialog dialog) {

            }
        });

    }

    private void releaseQAClient() {
        if (mClient != null) {
            mClient.release();
        }
        mClient = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.onPausePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayer.onResumePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.releasePlayer();
        releaseQAClient();
        mQA.release();
    }


    private void hideStatus() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void showStatus() {
        Rect outRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTextureView.getLayoutParams();
        params.height = outRect.bottom - outRect.top;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void showMessage(String nickName, String content) {
        NemoMessageEntity entity = new NemoMessageEntity();
        entity.setUserNickName(nickName);
        entity.setMessageContent(content);
        mListAdapter.addMessage(entity);
        mListAdapter.notifyDataSetChanged();
    }

    private void sendComment(String message) {
        if (TextUtils.isEmpty(message)) return;
        TextMessage textMessage = TextMessage.obtain(
                ChatParse.encode(Build.SERIAL,
                        message));
        if (mClient != null) {
            mClient.sendLiveChatMessage(textMessage, mSendMessage);
        }
    }

    private DefaultOnClick mOnClick = new DefaultOnClick() {
        @Override
        protected void onViewClick(View view) {
            if (view.getId() == R.id.imgb_chat) {
                showStatus();
                mRLSeedComment.setVisibility(View.VISIBLE);
                mCommentEdit.setFocusable(true);
                mCommentEdit.setFocusableInTouchMode(true);
                mCommentEdit.requestFocus();
                mIsShowSoftKey = true;
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mCommentEdit, InputMethodManager.SHOW_FORCED);
            } else if (view.getId() == R.id.btn_seed_comment) {
                sendComment(mCommentEdit.getText().toString().trim());
                hideSoftInput();
            } else if (view.getId() == R.id.imgb_help) {
                hideSoftInput();
                mDialog = new MessageDialog.Builder(LivePlayerActivity.this, true)
                        .setCloseListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                mDialog = null;
                            }
                        })
                        .show();
            } else if (view.getId() == R.id.imgb_back) {
                onActivityBack();
            }
        }
    };

    private void onActivityBack() {
        mDialog = new DefaultDialog.Builder(LivePlayerActivity.this)
                .setCancelOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        mDialog = null;
                        finish();
                    }
                })
                .setConfirmOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        mDialog = null;

                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        onActivityBack();
    }

    private void hideSoftInput() {
        if (!mIsShowSoftKey) return;
        mIsShowSoftKey = false;
        hideStatus();
        mCommentEdit.setText(null);
        mCommentEdit.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mCommentEdit.getWindowToken(), 0);
        mRLSeedComment.setVisibility(View.GONE);
    }

    private LQAClient.OnLQAEventListener mEventListener =
            new LQAClient.OnLQAEventListener() {
                @Override
                public void onSuccess(final int fromWhere) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (fromWhere) {
                                case LQAClient.IM_CHAT_CONNECT:
                                    KLog.d(TAG, "聊天室加入成功");
                                    break;
                                case LQAClient.IM_QA_CONNECT:
                                    //加入聊天房间，可以发送评论消息
                                    mClient.joinRoom(QAConfig.mChatRoom);
                                    ToastManager.showToast(LivePlayerActivity.this, "问答房间加入成功");
                                    break;
                                case LQAClient.QA_ANSWER:
                                    ToastManager.showToast(LivePlayerActivity.this, "答题成功");
                                    break;
                            }
                        }
                    });

                }

                @Override
                public void onError(final int fromWhere, final int error, final String errorInfo) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (fromWhere) {
                                case LQAClient.IM_CHAT_CONNECT:
                                    KLog.e(TAG, "聊天室加入失败，错误码：" + error + ",错误信息:" + errorInfo);
                                    break;
                                case LQAClient.IM_QA_CONNECT:
                                    KLog.e(TAG, "问答聊天室加入失败，错误码：" + error + ",错误信息:" + errorInfo);
                                    ToastManager.showToast(LivePlayerActivity.this, "问答房间加入失败，错误码：" + error);
                                    break;
                                case LQAClient.QA_ANSWER:
                                    mConfig.setUserContestStatus(false);
                                    KLog.e(TAG, "答题失败，错误码：" + error + ",错误信息:" + errorInfo);
                                    ToastManager.showToast(LivePlayerActivity.this, "答题失败，错误码:" + error);
                                    break;
                            }
                        }
                    });

                }
            };
    private IRongCallback.ISendMessageCallback mSendMessage =
            new IRongCallback.ISendMessageCallback() {

                @Override
                public void onAttached(Message message) {
                    KLog.e(TAG, "消息发送 onAttached");
                }

                @Override
                public void onSuccess(final Message message) {
                    KLog.e(TAG, "消息发送 onSuccess");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextMessage msg = (TextMessage) message.getContent();
                            if (msg != null) {
                                String[] strs = ChatParse.decode(msg.getContent());
                                if (strs != null)
                                    showMessage(strs[0], strs[1]);
                            }
                        }
                    });
                }

                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    KLog.e(TAG, "消息发送失败，错误码：" + errorCode.getValue() + ",错误信息：" + errorCode.getMessage());
                }
            };
    private LQAClient.OnLiveChatMessageListener mChatMessageListener =
            new LQAClient.OnLiveChatMessageListener() {
                @Override
                public void onMessageReceived(final Message message, int i) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextMessage msg = (TextMessage) message.getContent();
                            if (msg != null) {
                                String[] strs = ChatParse.decode(msg.getContent());
                                if (strs != null)
                                    showMessage(strs[0], strs[1]);
                            }
                        }
                    });

                }
            };

    private View.OnTouchListener mOnTouchListener =
            new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        hideSoftInput();
                    }
                    return false;
                }
            };
}
