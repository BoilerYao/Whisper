package com.boileryao.whisper;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by boiler-yao on 2016/10/10.
 *
 */

public class ConnectionTestActivity extends Activity {
    public static final String TAG = "ConnectionTestActivity";
    BluetoothService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_test);
        mService = BluetoothService.getInstance();
        Button send = (Button) findViewById(R.id.send);
        final TextView receive = (TextView) findViewById(R.id.receive);
        final MessageHandler handler = new MessageHandler(this, receive);
        new Thread() {
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
                    }
                    if (mService.isTurnedOff()) {
                        break;
                    }
                }
            }
        }.start();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.send(new QMessage("Hello, Bye", 0, 0));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mService.stop(false);
    }

    static class MessageHandler extends Handler{
        private final WeakReference<Activity> mActivityWeakReference;
        private TextView mTextView;
        MessageHandler(Activity activity, TextView textView) {
            mActivityWeakReference = new WeakReference<>(activity);
            mTextView = textView;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null && msg.obj != null) {
                Log.i(TAG, "handleMessage: " + msg.obj);
                mTextView.setText(msg.obj.toString());
            }
        }
    }
}