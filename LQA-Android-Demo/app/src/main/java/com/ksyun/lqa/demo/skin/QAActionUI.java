package com.ksyun.lqa.demo.skin;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.ksyun.live.qa.LQAClient;
import com.ksyun.live.qa.LQAConfig;
import com.ksyun.live.qa.common.model.LQAMatchResult;
import com.ksyun.live.qa.common.model.LQAQuestion;
import com.ksyun.live.qa.common.model.LQAResult;
import com.ksyun.live.qa.common.utils.KLog;
import com.ksyun.lqa.demo.QAConfig;
import com.ksyun.lqa.demo.R;
import com.ksyun.lqa.demo.skin.ui.LQADialog;
import com.ksyun.lqa.demo.skin.ui.MessageDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: [xiaoqiang]
 * @Description: [QAActionUI]
 * @CreateDate: [2018/1/11]
 * @UpdateDate: [2018/1/11]
 * @UpdateUser: [xiaoqiang]
 * @UpdateRemark: []
 */

public class QAActionUI implements LQAClient.OnLQAQuestionListener,
        LQAClient.OnLQAResultListener, LQAClient.OnLQAMatchResultListener {

    private static final String TAG = QAActionUI.class.getName();
    // 显示淘汰
    public final static int SHOW_ELIMINATE = 0;
    // 显示使用复活卡
    public final static int SHOW_USE_RESURGENCE = 1;
    // 显示你赢了
    public final static int SHOW_WIN = 2;
    // 显示最后结果,输了的情况
    public final static int SHOW_MATCH_RESULT = 3;

    private Context mContext;
    private Handler mHandler;
    private LQAClient mClient;
    private boolean isAnswer = true;
    private String mError;
    private List<LQAQuestion> mQuestion = new ArrayList<LQAQuestion>();
    private LQAConfig mConfig;
    private OnAnswerStatusListener mStatusListener;
    private OnDialogChangeListener mDialogChangeListener;
    private Dialog mMessage;
    // 用于保存最后的金额
    private float mWinMoney;
    // 用来保存是否要弹出消息提示，如果为-1 就不去进行提示
    private int mMessageStatus = -1;
    // 用于保存已使用复活卡，用于和SDK中的使用复活卡做判断，在答案弹出后判断是否要弹出使用复活卡的提示
    private int mUseReviveCardCount;
    // 用于是否显示过淘汰对话框
    private boolean isShowEliminatenum = true;
    // 完成了最后一局比赛，并且赢了
    private boolean isMatchWin = false;


    public QAActionUI(Context mContext, LQAClient mClient, LQAConfig mConfig) {
        this.mContext = mContext;
        mHandler = new Handler();
        this.mClient = mClient;
        this.mConfig = mConfig;
        isAnswer = mConfig.isUserContestStatus();
        KLog.w(TAG,"init isAnswer:"+isAnswer);
        KLog.i(TAG,"User Extra :"+ mConfig.getUserExtraLiveCount()+
                "，MAX User Extra:"+mConfig.getMaxExtraLiveUsedInContest());

        if (!isAnswer) {
            mError = "您已被淘汰，不能继续作答";
        }
        mUseReviveCardCount = mClient.getUsedReviveCardCount();
    }

    public void release() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    public void setOnAnswerStatusListener(OnAnswerStatusListener listener) {
        this.mStatusListener = listener;
    }

    public void setOnDialogChangeListener(OnDialogChangeListener mListener) {
        this.mDialogChangeListener = mListener;
    }

    @Override
    public void onShowQuestion(final LQAQuestion question) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                changeAnswer(question);
                KLog.w(TAG,"show Question，ID："+ question.getId()+
                        ",order："+question.getOrder()+"，totalNumber :"+question.getTotalNumber()+",isAnswer："+isAnswer);
                if (mMessage != null && mMessage.isShowing()) {
                    mMessage.dismiss();
                }
                LQADialog.Builder builder = new LQADialog.Builder(mContext)
                        .setCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                            }
                        })
                        .setQuestionStem(question.getOrder() + ". " + question.getTitle())
                        .setOptionsOK(isAnswer)
                        .setError(mError)
                        .setMaxDisplayTime(question.getShowPeriod() / 1000)
                        .setOnItemClickListener(new LQADialog.OnItemClickListener() {
                            @Override
                            public void onClick(String tag) {
                                mClient.answer(question.getId(), tag,
                                        QAConfig.mSignalingTimestamp
                                        , QAConfig.mRequestSignaling);
                            }
                        });
                for (String key : question.getOptions().keySet()) {
                    builder.addOptions(key + ":" + question.getOptions().get(key), key);
                }
                if (isAnswer) {
                    builder.setShowTimer(isAnswer, 0, null);
                } else {
                    builder.setShowTimer(false, R.drawable.lqa_time_gray_background, "观战");
                }
                mMessage = builder.show();
                if (mDialogChangeListener != null) {
                    mDialogChangeListener.showDialog(mMessage);
                }
                if (mStatusListener != null) {
                    mStatusListener.onAnswer(isAnswer);
                }
            }
        });
    }

    /**
     * 改变答题状态
     *
     * @param question
     */
    private void changeAnswer(LQAQuestion question) {
        mQuestion.add(question);
        if (mQuestion.get(0).getOrder() != 1 || question.getOrder() > mQuestion.size()) {
            KLog.e(TAG,"您错过了开始时间，不能作答");
            mError = "您错过了开始时间，不能作答";
            this.isAnswer = false;
            isShowEliminatenum = false;
            return;
        } else if (!mConfig.isUserContestStatus()) {
            this.isAnswer = mConfig.isUserContestStatus();
            KLog.e(TAG,"您已被淘汰，不能继续作答");
            mError = "您已被淘汰，不能继续作答";
            return;
        }
    }

    @Override
    public void onMatchResult(final LQAMatchResult matchResult) {
        mWinMoney = matchResult.getCashPrizes();
        Log.i(TAG, "最后的获奖名单" + Arrays.toString(matchResult.getUids().toArray()));
        if (isMatchWin) {
            mMessageStatus = SHOW_WIN;
        } else {
            mMessageStatus = SHOW_MATCH_RESULT;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showMessage(mMessageStatus);
            }
        });

    }

    @Override
    public void onResult(final LQAResult result) {
        KLog.w(TAG,"show Result，ID："+ result.getId()+
                ",order："+result.getOrder()+"，totalNumber :"+result.getTotalNumber()
                +",isAnswer："+isAnswer +",UsedReviveCardCount:"+mClient.getUsedReviveCardCount()
        +",success:"+(result.getCorrectOption().equals(result.getMeOption())));
        if (    // 没有使用复活卡
                mUseReviveCardCount == mClient.getUsedReviveCardCount()
                        // 答题失败了
                        && !result.getCorrectOption().equals(result.getMeOption())
                        // 还没有答到最后一题
                        && result.getOrder() < result.getTotalNumber()
                        // 允许答题状态下
                        && isAnswer

                ) {
            // 没有从第一题开始，不显示淘汰对话框
            if (mQuestion.size() <= 0 || mQuestion.get(0).getOrder() != 1) {
                isShowEliminatenum = false;
            }
            mError = "您已被淘汰，不能继续作答";
            this.isAnswer = false;
            mMessageStatus = SHOW_ELIMINATE;
            // 使用过复活卡
        } else if (mUseReviveCardCount < mClient.getUsedReviveCardCount()) {
            KLog.e(TAG,"use Revive Card");
            mUseReviveCardCount = mClient.getUsedReviveCardCount();
            mMessageStatus = SHOW_USE_RESURGENCE;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if (mMessage != null && mMessage.isShowing()) {
                    mMessage.dismiss();
                }
                if (mDialogChangeListener != null) {
                    mDialogChangeListener.hideDialog(mMessage);
                }
                showNormalResult(result);
                if (mStatusListener != null) {
                    mStatusListener.onAnswer(isAnswer);
                }
            }
        });
    }


    private void showNormalResult(LQAResult result) {
        isMatchWin = false;
        LQADialog.Builder builder = new LQADialog.Builder(mContext)
                .setCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (mMessageStatus >= 0) {
                            showMessage(mMessageStatus);
                        }
                    }
                })
                .setQuestionStem(result.getOrder() + ". " + result.getTitle())
                .setOptionsOK(false)
                .setMaxDisplayTime(result.getShowPeriod() / 1000);
        int maxNumber = 0;
        if (result.getCorrectOption().equalsIgnoreCase(result.getMeOption())) {
            KLog.i(TAG,"答题成功");
            builder.setShowTimer(false, R.drawable.lqa_answer_ok, null);
            if (result.getOrder() == result.getTotalNumber() && isAnswer) {
                KLog.i(TAG,"答到最后一道题");
                isMatchWin = true;
            }
        } else if (!isAnswer) {
            KLog.i(TAG,"不允许答题，你已经在观战模式了");
            builder.setShowTimer(false, R.drawable.lqa_time_gray_background, "观战");
        } else {
            KLog.w(TAG,"答错题了");
            builder.setShowTimer(false, R.drawable.lqa_answer_error, null);
        }
        if (result.getGroup() != null) {
            for (String key : result.getGroup().keySet()) {
                maxNumber += result.getGroup().get(key);
            }
        }
        for (String key : result.getOptions().keySet()) {
            String option = key + ":" + result.getOptions().get(key);
            int isOK = 2;
            if (key.equalsIgnoreCase(result.getCorrectOption())) {
                isOK = 1;
            } else if (key.equalsIgnoreCase(result.getMeOption())) {
                isOK = 0;
            } else {
                isOK = 2;
            }
            int num = 0;
            if (result.getGroup() != null && result.getGroup().get(key) != null) {
                num = result.getGroup().get(key);
            }
            builder.addOptions(option, isOK, num, maxNumber);
        }
        mMessage = builder.show();
        if (mDialogChangeListener != null) {
            mDialogChangeListener.showDialog(mMessage);
        }
    }


    /**
     * @param status 0 淘汰，1 使用复活卡 2 win
     */
    private void showMessage(int status) {
        if (!isShowEliminatenum && status == 0) {
            // 显示过已经被淘汰的的接口，不在显示
            return;
        }
        String message = "您被淘汰了~";
        int imageResource = R.drawable.lqa_spectators;
        String confirm = "继续观看";
        boolean time = false;
        switch (status) {
            case SHOW_ELIMINATE:
                isShowEliminatenum = false;
                message = "您被淘汰了~";
                imageResource = R.drawable.lqa_spectators;
                confirm = "继续观看";
                time = true;
                break;
            case SHOW_USE_RESURGENCE:
                message = "已为您自动使用复活卡";
                imageResource = R.drawable.lqa_kaml;
                confirm = "继续答题";
                time = true;
                break;
            case SHOW_WIN:
                message = "恭喜获得现金奖励";
                imageResource = R.drawable.lqa_win;
                confirm = mWinMoney + "元";
                time = false;
                break;
            case SHOW_MATCH_RESULT:
                message = "本场获胜奖励金额";
                imageResource = R.drawable.lqa_money;
                confirm = mWinMoney + "元";
                time = false;
                break;
            default:
                return;
        }
        if (mMessage != null && mMessage.isShowing()) {
            mMessage.dismiss();
        }
        mMessageStatus = -1;
        MessageDialog.Builder builder = new MessageDialog.Builder(mContext)
                .setMessage(message)
                .setImage(imageResource)
                .setConfirmText(confirm)
                .setHideTimer(time ? 5 : 0)
                .setConfirmListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMessage.dismiss();
                        if (mDialogChangeListener != null) {
                            mDialogChangeListener.hideDialog(mMessage);
                        }
                        mMessage = null;
                    }
                })
                .setCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                    }
                })
                .setCloseListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMessage.dismiss();
                        if (mDialogChangeListener != null) {
                            mDialogChangeListener.hideDialog(mMessage);
                        }
                        mMessage = null;
                    }
                });
        if (status == 2 || status == 3) {
            builder.setConfirmTextColor(0xffFC2F6B);
            builder.setConfirmTextSize(24);
        }
        mMessage = builder.show();
        if (mDialogChangeListener != null) {
            mDialogChangeListener.showDialog(mMessage);
        }
    }

    public interface OnAnswerStatusListener {
        void onAnswer(boolean isAnswer);
    }

    public interface OnDialogChangeListener {
        void showDialog(Dialog dialog);

        void hideDialog(Dialog dialog);
    }
}
