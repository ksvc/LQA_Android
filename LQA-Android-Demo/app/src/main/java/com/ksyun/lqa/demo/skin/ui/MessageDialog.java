package com.ksyun.lqa.demo.skin.ui;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksyun.lqa.demo.R;

/**
 * @Author: [xiaoqiang]
 * @Description: [提示消息]
 * @CreateDate: [2018/1/9]
 * @UpdateDate: [2018/1/9]
 * @UpdateUser: [xiaoqiang]
 * @UpdateRemark: []
 */

public class MessageDialog extends Dialog {

    private ImageView mClose;
    private ImageView mStatus;
    private TextView mStatusText;
    private TextView mConfirmText;
    private int mHideTimer;
    private Handler mHandler;
    private Dialog.OnCancelListener mDialogCancelListener;

    public MessageDialog(@NonNull Context context, boolean showHelp) {
        super(context, showHelp ? R.style.DefaultDailogTheme : R.style.DailogTheme);
        if (showHelp) {
            setContentView(R.layout.dialog_help);
            mClose = findViewById(R.id.imgv_close);
        } else {
            setContentView(R.layout.dialog_message);
            mClose = findViewById(R.id.imgv_close);
            mStatus = findViewById(R.id.imgv_status);
            mStatusText = findViewById(R.id.tv_status);
            mConfirmText = findViewById(R.id.tv_confirm);
        }
        mHandler = new Handler();
        setOnCancelListener(mCancelListener);
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (getWidth(getContext()) * 0.93);
        lp.height = (int) (getWidth(getContext()) * 0.94);
        getWindow().setAttributes(lp);
        if (mHideTimer > 0)
            mHandler.postDelayed(mDelayRunnable, 1000);
    }

    private Dialog.OnCancelListener mCancelListener = new OnCancelListener() {
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
    private Runnable mDelayRunnable = new Runnable() {
        @Override
        public void run() {
            mHideTimer--;
            if (mHideTimer > 0 && isShowing()) {
                mHandler.postDelayed(mDelayRunnable, 1000);
            } else {
                MessageDialog.this.dismiss();
                if (mDialogCancelListener != null) {
                    mDialogCancelListener.onCancel(MessageDialog.this);
                }
            }
        }
    };

    public static int getWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getWidth();
    }

    public static class Builder {
        private MessageDialog mDialog;

        public Builder(Context mContext) {
            mDialog = new MessageDialog(mContext, false);
        }

        public Builder(Context mContext, boolean showHelp) {
            mDialog = new MessageDialog(mContext, showHelp);
        }

        public Builder setImage(int imageResource) {
            mDialog.mStatus.setImageResource(imageResource);
            return this;
        }

        public Builder setMessage(String message) {
            mDialog.mStatusText.setText(message);
            return this;
        }

        public Builder setConfirmText(String confirm) {
            mDialog.mConfirmText.setText(confirm);
            return this;
        }

        public Builder setConfirmTextColor(int color) {
            mDialog.mConfirmText.setTextColor(color);
            return this;
        }

        public Builder setCloseListener(View.OnClickListener mClick) {
            mDialog.mClose.setOnClickListener(mClick);
            return this;
        }

        public Builder setConfirmListener(View.OnClickListener mClick) {
            mDialog.mConfirmText.setOnClickListener(mClick);
            return this;
        }

        public Builder setConfirmTextSize(int dp) {
            mDialog.mConfirmText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dp);
            return this;
        }

        public Builder setCancelListener(Dialog.OnCancelListener mCancelListener) {
//            mDialog.setOnCancelListener(mCancelListener);
            mDialog.mDialogCancelListener = mCancelListener;
            return this;
        }

        public Builder setHideTimer(int time) {
            mDialog.mHideTimer = time;
            return this;
        }

        public Dialog show() {
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
            return mDialog;
        }

    }


}
