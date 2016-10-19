package com.boileryao.whisper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import static android.content.ContentValues.TAG;

/**
 * Created by boiler-yao on 2016/10/16.
 * Dialog Activity
 */

public class DialogActivity extends AppCompatActivity implements View.OnClickListener {
    MessageListAdapter adapter;
    RecyclerView recyclerViewMessages;
    BluetoothService mService;
    EditText editMessage;
    final MessageHandler handler = new MessageHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        recyclerViewMessages = (RecyclerView) findViewById(R.id.rv_dialog);
        mService = BluetoothService.getInstance();
        adapter = new MessageListAdapter(this);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(adapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getIntent().getStringExtra("name"));
            actionBar.setSubtitle(getIntent().getStringExtra("address"));
        }
        ImageButton send = (ImageButton) findViewById(R.id.send);
        editMessage = (EditText) findViewById(R.id.edit_message);
        send.setOnClickListener(this);
        adapter.append(new QMessage("Wireshark", 0, QMessage.TYPE_RECEIVED));
        adapter.append(new QMessage("Uninstall Information", 0, QMessage.TYPE_RECEIVED));
        adapter.append(new QMessage("Windows Multimedia Platform", 0, QMessage.TYPE_RECEIVED));
        adapter.append(new QMessage("Typora", 0, QMessage.TYPE_RECEIVED));
        adapter.append(new QMessage("腾讯游戏", 0, QMessage.TYPE_SENT));
        adapter.append(new QMessage("Microsoft Office 15", 0, QMessage.TYPE_RECEIVED));
        adapter.append(new QMessage("nodejs", 0, QMessage.TYPE_SENT));
        new MessageThread().start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO: 2016/10/16 notify the other side 
        mService.stop(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                QMessage msg = new QMessage(editMessage.getText().toString()
                        , System.currentTimeMillis(), QMessage.TYPE_SENT);
                if (mService != null && mService.isAvailable()) {
                    mService.send(msg);
                }
                appendAndNotify(msg);
                break;
        }
    }

    void appendAndNotify(QMessage msg) {
        adapter.append(msg);
        recyclerViewMessages.smoothScrollToPosition(adapter.getItemCount());
    }

    private class MessageThread extends Thread {
        @Override
        public void run() {
            super.run();
            Log.d(TAG, "接收消息的线程开始运行……");
            while (true) {
                if (mService.isAvailable()) {
                    QMessage content = mService.receive();
                    Message msg = new Message();
                    msg.obj = content;
                    handler.sendMessage(msg);
                    if (mService.isTurnedOff() || content.getContent() == null) {
                        break;
                    }
                }
            }
        }
    }

    static class MessageHandler extends Handler {
        private final DialogActivity dialogActivity;

        MessageHandler(DialogActivity activity) {
            dialogActivity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null && msg.obj != null) {
                dialogActivity.appendAndNotify((QMessage) msg.obj);
            }
        }
    }
}
