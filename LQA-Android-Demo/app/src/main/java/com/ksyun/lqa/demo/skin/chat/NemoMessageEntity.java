package com.ksyun.lqa.demo.skin.chat;

/**
 * 消息体
 * Created by xbc on 2016/12/9.
 */

public class NemoMessageEntity {

    private String mNickName;

    private String mMessage;

    public void setUserNickName(String name) {
        mNickName = name;
    }

    public void setMessageContent(String msg) {
        mMessage = msg;
    }

    public String getUserNickName() {
        return mNickName;
    }

    public String getMessageContent() {
        return mMessage;
    }
}
