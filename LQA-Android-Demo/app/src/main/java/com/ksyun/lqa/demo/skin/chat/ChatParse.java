package com.ksyun.lqa.demo.skin.chat;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Author: [xiaoqiang]
 * @Description: [ChatParse]
 * @CreateDate: [2018/1/10]
 * @UpdateDate: [2018/1/10]
 * @UpdateUser: [xiaoqiang]
 * @UpdateRemark: []
 */

public class ChatParse {
    public static String encode(String uid, String message) {
        JSONObject object = new JSONObject();
        try {
            object.put("uid", uid);
            object.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }

    public static String[] decode(String message) {
        try {
            JSONObject object = new JSONObject(message);
            String[] strings = new String[2];
            strings[0] = object.getString("uid");
            strings[1] = object.getString("message");
            return strings;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }
}
