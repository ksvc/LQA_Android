package com.ksyun.lqa.demo.skin.util;

import android.content.Context;
import android.widget.Toast;

/**
 * @Author: [xiaoqiang]
 * @Description: [ToastManager]
 * @CreateDate: [2018/1/9]
 * @UpdateDate: [2018/1/9]
 * @UpdateUser: [xiaoqiang]
 * @UpdateRemark: []
 */

public class ToastManager {
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
