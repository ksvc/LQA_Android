package com.ksyun.lqa.demo.skin.chat;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ksyun.lqa.demo.R;

import java.util.ArrayList;

/**
 * Created by xbc on 2016/12/2.
 */

public class LiveChatListAdapter extends BaseAdapter {

    private static final int MAX_MESSAGE_NUM = 40; // 最大存储的消息数目

    private Context mContext;
    private ListView mListView;
    private ArrayList<NemoMessageEntity> mMessageList;
    private int mResourceID;

    public LiveChatListAdapter(Context context, ListView listView) {
        mContext = context;
        mMessageList = new ArrayList<>();
        mResourceID = R.layout.nemo_msg_list_item;
        mListView = listView;
    }

    public void addMessage(NemoMessageEntity entity) {
        mMessageList.add(entity);

        // 消息数量超过上限,则删除第一个item
        if (mMessageList.size() > MAX_MESSAGE_NUM)
            mMessageList.remove(0);
    }

    @Override
    public int getCount() {
        if (mMessageList != null)
            return mMessageList.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (mMessageList != null)
            return mMessageList.get(i);

        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view != null) {
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(mResourceID, null);
            viewHolder = new ViewHolder();

            viewHolder.mMsgListItem = (TextView) view.findViewById(R.id.msg_chat_item);

            view.setTag(viewHolder);
        }

        NemoMessageEntity entity = mMessageList.get(i);
        SpannableStringBuilder spannable = createMessageItem(entity);

        viewHolder.mMsgListItem.setText(spannable);

        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

        mListView.post(new Runnable() {
            @Override
            public void run() {
                mListView.setSelection(mListView.getCount() - 1);
            }
        });
    }

    private class ViewHolder {
        TextView mMsgListItem;
    }


    private SpannableStringBuilder createMessageItem(NemoMessageEntity entity) {
        int spanLen = entity.getUserNickName().length() + 1;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(entity.getUserNickName() + " ");

        spannableStringBuilder.setSpan(new ForegroundColorSpan(0xFFBBBAFF), 0, spanLen, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        spannableStringBuilder.append(entity.getMessageContent());

        spannableStringBuilder.setSpan(new ForegroundColorSpan(0xffFFFFFF), spanLen, spanLen + entity.getMessageContent().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }
}
