package com.ksyun.lqa.demo.skin.util;

import android.view.View;

/**
 * Created by gaoyuanpeng on 2017/2/23.
 */

public abstract class DefaultOnClick implements View.OnClickListener {
    private static long mLastTimer;

    @Override
    public void onClick(View view) {
        if (System.currentTimeMillis() - mLastTimer < 500) return;
        mLastTimer = System.currentTimeMillis();
        onViewClick(view);
    }

    protected abstract void onViewClick(View view);
}
