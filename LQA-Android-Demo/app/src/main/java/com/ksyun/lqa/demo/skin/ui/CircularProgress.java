package com.ksyun.lqa.demo.skin.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.ksyun.lqa.demo.R;


/**
 * Created by xiaoqiang on 2017/10/21.
 */

public class CircularProgress extends View {
    private Paint mPaint;
    protected float mCircleWidth = 3;
    private int mIndeterminateColor = Color.TRANSPARENT;
    private int mArcColor = Color.BLACK;
    private float mProgress;
    private float mStartAngle = -45;
    protected RectF mOval;
    private Paint mTextPaint;
    private String mText;
    private int mTextSize;
    private int mTextColor;
    private int mBitmapResource;
    private Bitmap mBitmap;
    private Paint mBitmapPanit;

    public CircularProgress(Context context) {
        super(context);
        initParams(null);
    }

    public CircularProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParams(attrs);
    }

    public CircularProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams(attrs);
    }

    protected void initParams(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CircularProgress);
            mCircleWidth = ta.getDimension(R.styleable.CircularProgress_circle_width, 3);
            mIndeterminateColor = ta.getColor(R.styleable.CircularProgress_indeterminate_color, Color.TRANSPARENT);
            mArcColor = ta.getColor(R.styleable.CircularProgress_arc_color, Color.BLACK);
            mStartAngle = ta.getFloat(R.styleable.CircularProgress_start_angle, -45f);
            mTextColor = ta.getColor(R.styleable.CircularProgress_textColor, 0xffffffff);
            mTextSize = ta.getDimensionPixelSize(R.styleable.CircularProgress_textSize, 40);
            mBitmapResource = ta.getResourceId(R.styleable.CircularProgress_src, -1);
            ta.recycle();
        }

        mOval = null;
        mPaint = new Paint();
        mPaint.setAntiAlias(true); //消除锯齿
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mCircleWidth);
        if (mBitmapResource >= 0) {
            mBitmap = BitmapFactory.decodeResource(getResources(), mBitmapResource);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        computerSize();
        drawBitmap(canvas);
        drawText(canvas);
        drawProgress(canvas);

    }

    protected void computerSize() {
        if (mOval == null) {
            float left;
            float right;
            float top;
            float bottom;
            int width = getWidth() - getPaddingLeft() - getPaddingRight();
            int height = getHeight() - getPaddingTop() - getPaddingBottom();
            if (width > height) {
                left = getPaddingLeft() + (width - height) / 2.0f;
                right = getWidth() - getPaddingRight() - (width - height) / 2.0f;
                top = getPaddingTop();
                bottom = getHeight() - getPaddingBottom();
            } else {
                left = getPaddingLeft();
                right = getWidth() - getPaddingRight();
                top = getPaddingTop() + (height - width) / 2.0f;
                bottom = getHeight() - (height - width) / 2.0f - getPaddingBottom();
            }
            mOval = new RectF(left, top, right, bottom);
        }
    }

    protected void drawBitmap(Canvas canvas) {

        if (mBitmap != null) {
            if (mBitmapPanit == null) {
                mBitmapPanit = new Paint();
                mBitmapPanit.setAntiAlias(true);
            }
            canvas.drawBitmap(mBitmap, null, mOval, mBitmapPanit);
        }
    }

    protected void drawText(Canvas canvas) {
        if (!TextUtils.isEmpty(mText)) {
            if (mTextPaint == null) {
                mTextPaint = new Paint();
                mTextPaint.setColor(mTextColor);
                mTextPaint.setTextAlign(Paint.Align.CENTER);
                mTextPaint.setAntiAlias(true); //消除锯齿
                mTextPaint.setStrokeWidth(3);
                mTextPaint.setTextSize(mTextSize);
            }
            canvas.drawText(mText, mOval.centerX(), mOval.centerY() + mTextPaint.getTextSize() / 2, mTextPaint);
        }
    }

    protected void drawProgress(Canvas canvas) {
        mPaint.setColor(mIndeterminateColor);
        canvas.drawArc(mOval, 360 * mProgress, 360, false, mPaint);

        mPaint.setColor(mArcColor);
        canvas.drawArc(mOval, mStartAngle, 360 * mProgress, false, mPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBitmap != null && mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        postInvalidate();
    }

    public void setProgress(float progress, String text) {
        this.mProgress = progress;
        this.mText = text;
        postInvalidate();
    }

    public void setCircleWidth(int mCircleWidth) {
        this.mCircleWidth = mCircleWidth;
    }

    public void setIndeterminateColor(int mIndeterminateColor) {
        this.mIndeterminateColor = mIndeterminateColor;
    }

    public void setArcColor(int mArcColor) {
        this.mArcColor = mArcColor;
    }

    public void setStartAngle(int mStartAngle) {
        this.mStartAngle = mStartAngle;
    }
}
