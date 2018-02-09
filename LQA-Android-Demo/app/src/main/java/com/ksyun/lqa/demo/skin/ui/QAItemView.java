package com.ksyun.lqa.demo.skin.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

/**
 * @Author: [xiaoqiang]
 * @Description: [QAItemView]
 * @CreateDate: [2018/1/19]
 * @UpdateDate: [2018/1/19]
 * @UpdateUser: [xiaoqiang]
 * @UpdateRemark: []
 */

public class QAItemView extends RelativeLayout {
    private Context mContext;
    private View mBackground;
    private View mOvercover;
    private View mFinalize;
    private int mDefaultColor = 0xFF00F0F4;
    private int mDefaultCoverColor = 0xffffffff;
    private int mDefaultBackground = 0xffffeee0;
    private GradientDrawable mFinalizeDrawable;
    private GradientDrawable mBackgroundDrawable;
    private GradientDrawable mOvercoverDrawable;
    private int mRadius = 30;
    private int mProgress = 0;
    private int mMax = 100;
    private int mWidth;
    private int mStroke;

    public QAItemView(@NonNull Context context) {
        super(context);
        initView();
    }

    public QAItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public QAItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mContext = getContext();
        mBackground = new View(mContext);
        mOvercover = new View(mContext);
        mFinalize = new View(mContext);
        RelativeLayout.LayoutParams mParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        addView(mBackground, mParams);
        mParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        addView(mOvercover, mParams);
        mParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        addView(mFinalize, mParams);
        mStroke = dip2px(2);
        mRadius = dip2px(30);
        reLoadView();
    }

    public void reLoadView() {
        mBackgroundDrawable = new GradientDrawable();
        mBackgroundDrawable.setCornerRadius(dip2px(mRadius));
        mBackgroundDrawable.setColor(mDefaultColor);
        RelativeLayout.LayoutParams params = (LayoutParams) mBackground.getLayoutParams();
        params.leftMargin = mStroke;
        params.topMargin = mStroke;
        params.bottomMargin = mStroke;
        params.rightMargin = mStroke;
        mBackground.setBackground(mBackgroundDrawable);
        mBackground.setLayoutParams(params);


        mOvercoverDrawable = new GradientDrawable();
        int radius = dip2px(mRadius);
        float[] radiis = new float[]{0,
                0, radius, radius,
                radius, radius, 0,
                0};
        mOvercoverDrawable.setCornerRadii(radiis);
        mOvercoverDrawable.setColor(mDefaultBackground);
        mOvercover.setBackground(mOvercoverDrawable);

        mFinalizeDrawable = new GradientDrawable();
        mFinalizeDrawable.setCornerRadius(dip2px(mRadius));
        mFinalizeDrawable.setStroke(mStroke + 1, mDefaultCoverColor);
        mFinalize.setBackground(mFinalizeDrawable);
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        if (mWidth <= 0) {
            mWidth = getWidth();
        }
        post(mProgressChangeRunnable);
    }


    public void setColor(int mDefaultColor) {
        this.mDefaultColor = mDefaultColor;
    }


    public void setFrameColor(int mDefaultCoverColor) {
        this.mDefaultCoverColor = mDefaultCoverColor;
    }


    public void setAmbientColor(int mDefaultBackground) {
        this.mDefaultBackground = mDefaultBackground;
    }


    public void setRadius(int mRadius) {
        this.mRadius = mRadius;
    }

    public void setMax(int mMax) {
        this.mMax = mMax;
        if (mMax <= 0) mMax = 1;
    }

    private Runnable mProgressChangeRunnable = new Runnable() {
        @Override
        public void run() {
            if (mWidth <= 0) {
                getViewTreeObserver().addOnPreDrawListener(
                        new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                mWidth = getMeasuredWidth();
                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mOvercover.getLayoutParams();
                                params.leftMargin = (int) (mWidth * (mProgress / (float) mMax));
                                return true;
                            }
                        });
            } else {
                RelativeLayout.LayoutParams params =
                        (RelativeLayout.LayoutParams) mOvercover.getLayoutParams();
                params.leftMargin = (int) (mWidth * (mProgress / (float) mMax));
                mOvercover.setLayoutParams(params);
            }
        }
    };

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        post(mProgressChangeRunnable);
    }

    private int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    static class Builder {
        private QAItemView mLayerButton;

        public Builder(Context mContext) {
            mLayerButton = new QAItemView(mContext);
        }

        public Builder setColor(int color) {
            mLayerButton.setColor(color);
            return this;
        }

        public Builder setFrameColor(int color) {
            mLayerButton.setFrameColor(color);
            return this;
        }

        public Builder setAmbientColor(int color) {
            mLayerButton.setAmbientColor(color);
            return this;
        }

        public QAItemView build() {
            mLayerButton.reLoadView();
            return mLayerButton;
        }

    }
}
