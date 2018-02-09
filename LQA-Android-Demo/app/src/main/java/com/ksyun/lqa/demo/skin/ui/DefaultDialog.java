package com.ksyun.lqa.demo.skin.ui;


import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
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

public class DefaultDialog extends Dialog {
    private Context mContext;
    private TextView mConfirm;
    private TextView mCancel;


    public DefaultDialog(@NonNull Context context) {
        super(context, R.style.DailogTheme);
        setContentView(R.layout.dialog_default);
        mContext = context;
        mConfirm = findViewById(R.id.tv_confirm);
        mCancel = findViewById(R.id.tv_cancel);
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = dip2px(mContext, 220);
        lp.height = dip2px(mContext, 120);
        getWindow().setAttributes(lp);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static int getWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getWidth();
    }

    public static class Builder {
        private DefaultDialog mDialog;

        public Builder(Context mContext) {
            mDialog = new DefaultDialog(mContext);
        }


        public Builder setConfirmOnClick(View.OnClickListener mclick){
            mDialog.mConfirm.setOnClickListener(mclick);
            return this;
        }

        public Builder setCancelOnClick(View.OnClickListener mclick){
            mDialog.mCancel.setOnClickListener(mclick);
            return this;
        }

        public Dialog show() {
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
            return mDialog;
        }

    }


}
