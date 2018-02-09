package com.ksyun.lqa.demo.skin.ui;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ksyun.lqa.demo.R;
import com.ksyun.lqa.demo.skin.util.DefaultOnClick;

/**
 * @Author: [xiaoqiang]
 * @Description: [LQADialog]
 * @CreateDate: [2018/1/9]
 * @UpdateDate: [2018/1/9]
 * @UpdateUser: [xiaoqiang]
 * @UpdateRemark: []
 */

public class LQADialog extends Dialog {

    private TextView mQuestionStem;
    private LinearLayout mQuestionSeleter;
    private RelativeLayout mQuestionBackgroud;
    private boolean mIsSelector;
    private CircularProgress mProgress;
    private int mMaxTime = 10;
    private int mCurrentTime;
    private Handler mHandler;
    private TextView mTimerView;
    private boolean mIsAnswer;
    private TextView mError;
    private OnCancelListener mDialogCancelListener;
    private OnItemClickListener mOnItemClickListener;
    private boolean isShowCountdown = true;
    private int mImageResource;
    private String mShowText;
    private boolean mIsShowProgress;

    public LQADialog(@NonNull Context context) {
        super(context, R.style.DailogTheme);
        setContentView(R.layout.dialog_qa);
        mQuestionSeleter = findViewById(R.id.ll_question_seleter);
        mQuestionStem = findViewById(R.id.tv_stem);
        mQuestionBackgroud = findViewById(R.id.rl_question_background);
        mProgress = findViewById(R.id.circula_progress);
        mHandler = new Handler();
        mTimerView = findViewById(R.id.tv_time);
        mError = findViewById(R.id.tv_error);
        setOnCancelListener(mCancelListener);
    }

    @Override
    public void show() {
        super.show();
        mIsSelector = false;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (getWidth(getContext()) * 0.93);
        lp.y = dip2px(getContext(), 40);
        getWindow().setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        getWindow().setAttributes(lp);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = dip2px(getContext(), 20);
        mQuestionBackgroud.setLayoutParams(params);
        uploadTime();
    }


    private void uploadTime() {
        mCurrentTime = mMaxTime;
        mProgress.setProgress(((float) (mMaxTime - mCurrentTime)) / mMaxTime);
        showCountdown(mCurrentTime);
        mHandler.postDelayed(mDelayRunnable, 1000);
    }

    private void showCountdown(int time) {
        mTimerView.setBackgroundResource(mImageResource);
        if (isShowCountdown) {
            mTimerView.setText(String.valueOf(time + 1));
            if (time == 0) {
                mTimerView.setText(null);
                mTimerView.setBackgroundResource(R.drawable.lqa_no_time);
            }
        } else {
            mTimerView.setText(mShowText);
            mTimerView.setTextColor(0xffffffff);
            mTimerView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        }
    }

    private Runnable mDelayRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCurrentTime >= 0) {
                if (mIsShowProgress) {
                    mProgress.setArcColor((mCurrentTime <= 3) ? 0xffFC316C : 0xff42CBF2);
                    mProgress.setProgress(((float) (mMaxTime - mCurrentTime)) / mMaxTime);
                } else {
                    mProgress.setProgress(0);
                }
                showCountdown(mCurrentTime);
                mCurrentTime--;
                mHandler.postDelayed(mDelayRunnable, 1000);
            } else {
                if (isShowCountdown) {
                    mTimerView.setBackgroundResource(R.drawable.lqa_no_time);
                }
                if (mDialogCancelListener != null) {
                    mDialogCancelListener.onCancel(LQADialog.this);
                }
                LQADialog.this.dismiss();
            }
        }
    };
    private OnCancelListener mCancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
            }
            mHandler = null;
            if (mDialogCancelListener != null) {
                mDialogCancelListener.onCancel(dialog);
            }
        }
    };

    public static int getWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getWidth();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int mItemColor = 0xFFEDF0F4;
    private int mFramColor = 0xFFEDF0F4;
    private boolean isAllowClick = true;
    private int mItemProgress = 100;
    private int mItemMax = 100;

    private QAItemView addOptions(String options) {
        QAItemView item = (QAItemView) View.inflate(getContext(), R.layout.options_item2, null);
        item.setColor(mItemColor);
        item.setFrameColor(mFramColor);
        item.setAmbientColor(0xffffffff);
        item.setProgress(mItemProgress);
        item.setMax(mItemMax);
        ((TextView) item.findViewById(R.id.tv_options)).setText(options);

        int height = (int) getContext().getResources().getDimension(R.dimen.lqa_options_item_width);
        height += options.length() / 18 * dip2px(getContext(), 14);
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        height);


        layoutParams.topMargin = dip2px(getContext(), 10);
        if (isAllowClick) {
            item.setOnClickListener(mOnClick);
        }
        item.reLoadView();
        mQuestionSeleter.addView(item, layoutParams);
        return item;
    }

    private DefaultOnClick mOnClick = new DefaultOnClick() {
        @Override
        protected void onViewClick(View view) {
            if (!mIsAnswer) {
                mError.setVisibility(View.VISIBLE);
            } else if (!mIsSelector) {
                QAItemView item = (QAItemView) view;
                item.setColor(0xFF42CBF2);
                item.setFrameColor(0xFF42CBF2);
                item.setProgress(100);
                item.reLoadView();
                ((TextView) item.findViewById(R.id.tv_options)).
                        setTextColor(view.getResources().getColor(R.color.gray));
                mIsSelector = true;
                if (mOnItemClickListener != null && view.getTag() != null) {
                    mOnItemClickListener.onClick((String) view.getTag());
                }
            }
        }
    };

    public static class Builder {
        private LQADialog mDialog;

        public Builder(Context mContext) {
            mDialog = new LQADialog(mContext);
        }

        public Builder setQuestionStem(String question) {
            mDialog.mQuestionStem.setText(question);
            return this;
        }

        public Builder addOptions(String options, String tag) {
            View item = mDialog.addOptions(options);
            item.setTag(tag);
            return this;
        }

        public Builder setOptionsOK(boolean mIsAnswer) {
            mDialog.mIsAnswer = mIsAnswer;
            return this;
        }

        public Builder setShowTimer(boolean isShow, int imageResource, String text) {
            if (imageResource <= 0) {
                imageResource = R.drawable.lqa_time_background;
            }
            mDialog.isShowCountdown = isShow;
            mDialog.mIsShowProgress = true;
            mDialog.mImageResource = imageResource;
            mDialog.mShowText = text;
            return this;
        }

        /**
         * @param options
         * @param ok      int 1:回答正确 0:回答错误 other 其他状态
         * @param number
         * @return
         */
        public Builder addOptions(String options, int ok, int number, int max) {

            mDialog.mIsShowProgress = false;

            if (ok == 1) {
                mDialog.mItemColor = 0xFF42CBF2;
            } else if (ok == 0) {
                mDialog.mItemColor = 0xFFFFB4CA;
            } else {
                mDialog.mItemColor = 0xFFEDF0F4;
            }
            mDialog.mFramColor = 0x99E7E8E8;
            mDialog.mItemProgress = number;
            mDialog.mItemMax = max;
            mDialog.isAllowClick = false;

            final View item = mDialog.addOptions(options);
            ((TextView) item.findViewById(R.id.tv_options)).
                    setTextColor(item.getResources().getColor(R.color.gray));
            TextView numver = ((TextView) item.findViewById(R.id.tv_number));
            numver.setText(String.valueOf(number));
            numver.setVisibility(View.VISIBLE);
            return this;
        }

        public Builder setMaxDisplayTime(int time) {
            mDialog.mMaxTime = time - 1;
            return this;
        }

        public Builder setError(String error) {
            if (!TextUtils.isEmpty(error)) {
                mDialog.mError.setText(error);
            }
            return this;
        }

        public Builder setOnItemClickListener(OnItemClickListener mItemListener) {
            mDialog.mOnItemClickListener = mItemListener;
            return this;
        }

        public Builder setCancelListener(OnCancelListener mCancelListener) {
            mDialog.mDialogCancelListener = mCancelListener;
            return this;
        }

        public Dialog show() {
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                        return true;
                    }
                    return false;
                }
            });
            mDialog.show();
            return mDialog;
        }
    }

    public interface OnItemClickListener {
        void onClick(String tag);
    }

}
