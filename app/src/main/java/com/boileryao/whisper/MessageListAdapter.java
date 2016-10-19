package com.boileryao.whisper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boiler-yao on 2016/10/15.
 * RecyclerView Message Adapter
 */

class MessageListAdapter extends RecyclerView.Adapter {
    private List<QMessage> mQMessages = new ArrayList<>();
    private Context mContext;

    MessageListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case QMessage.TYPE_RECEIVED:
                v = LayoutInflater.from(mContext).inflate(R.layout.item_msg_received, parent, false);
                break;
            case QMessage.TYPE_SENT:
                v = LayoutInflater.from(mContext).inflate(R.layout.item_msg_sent, parent, false);
                break;
        }
        return new MsgViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MsgViewHolder) holder).content.setText(mQMessages.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mQMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mQMessages.get(position).getType();
    }

    public void append(QMessage msg) {
        if (msg != null) {
            mQMessages.add(msg);
            this.notifyDataSetChanged();
        }
    }
    private static class MsgViewHolder extends RecyclerView.ViewHolder {
        TextView content;

        MsgViewHolder(android.view.View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }
}
