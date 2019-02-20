package com.app.shovelerapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.shovelerapp.R;
import com.app.shovelerapp.doc.Globals;
import com.app.shovelerapp.model.ChatModel;
import com.app.shovelerapp.service.ILoadService;
import com.app.shovelerapp.service.ServiceManager;
import com.app.shovelerapp.utils.SharedPrefClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Administrator on 4/11/2017.
 */
public class ChatActivity extends AppCompatActivity implements View.OnClickListener,ILoadService {


    public EditText editMessage;
    public ScrollView scrChat;
    public LinearLayout layoutChat;
    public ImageView imgSend;

    public Thread mMessageLoadThread;
    public boolean isLoad = true;
    private SharedPrefClass prefClass;

    @Override
    public void onResponse(int code) {
        switch (code)
        {
            case 100:
                Intent intent = new Intent();
                intent.setAction("com.songu.shovler.chatscreen");
                ChatActivity.this.sendBroadcast(intent);
                break;
            case 1://loaded message
                break;
        }
    }

    @Override
    public void onResponse(int code, int index) {

    }


    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            addChatViews();
        }
    }
    public MyReceiver broadcastReceiver  = new MyReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);


        init();
        prefClass = new SharedPrefClass(ChatActivity.this);
        openThread();
        this.registerReceiver(broadcastReceiver, new IntentFilter("com.songu.shovler.chatscreen"));

    }
    public void loadMessage()
    {
        ServiceManager.onLoadChatHistory(prefClass.getSavedStringPreference(SharedPrefClass.USER_ID),this);
    }
    public void init()
    {
        editMessage = (EditText) this.findViewById(R.id.editChatMessage);
        imgSend = (ImageView) this.findViewById(R.id.imgChatSend);
        layoutChat = (LinearLayout) this.findViewById(R.id.layoutChat);
        scrChat = (ScrollView) this.findViewById(R.id.scrChat);

        imgSend.setOnClickListener(this);
    }
    public void openThread()
    {
        mMessageLoadThread = new Thread()
        {
            public void run()
            {
                while(isLoad)
                {
                    loadMessage();
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mMessageLoadThread.start();
    }

    public void addChatViews()
    {
        layoutChat.removeAllViews();
        if (Globals.g_lstMessages.size() > 0)
        {
            for (int i = 0;i < Globals.g_lstMessages.size();i++)
            {
                if (Globals.g_lstMessages.get(i).mFrom.equals("1"))
                {//my message
                    addSenderView(Globals.g_lstMessages.get(i));
                }
                else
                {
                    addReceiverView(Globals.g_lstMessages.get(i));
                }
            }
        }
    }
    public void addReceiverView(ChatModel cModel)
    {
        final View localView;
        MessageHolder localViewHolder = null;

        localView = LayoutInflater.from(this).inflate(R.layout.layout_chat_from, null);

        localViewHolder = new MessageHolder();
        localViewHolder.txtMessage = (TextView) localView.findViewById(R.id.txtChatFrom);
        localViewHolder.txtTime = (TextView) localView.findViewById(R.id.txtChatFromTime);
        localView.setTag(localViewHolder);
        localViewHolder.txtMessage.setText(cModel.mMessage);
        localViewHolder.txtTime.setText(getDateCurrentTimeZone(Long.parseLong(cModel.mTime)));
        layoutChat.addView(localView);

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {Thread.sleep(100);} catch (InterruptedException e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrChat.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }).start();
    }
    public void addSenderView(ChatModel cModel)
    {
        final View localView;
        MessageHolder localViewHolder = null;

        localView = LayoutInflater.from(this).inflate(R.layout.layout_chat_to, null);

        localViewHolder = new MessageHolder();
        localViewHolder.txtMessage = (TextView) localView.findViewById(R.id.txtChatReceiver);
        localViewHolder.txtTime = (TextView) localView.findViewById(R.id.txtChatToTime);
        localView.setTag(localViewHolder);
        localViewHolder.txtMessage.setText(cModel.mMessage);
        localViewHolder.txtTime.setText(getDateCurrentTimeZone(Long.parseLong(cModel.mTime)));
        layoutChat.addView(localView);

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {Thread.sleep(100);} catch (InterruptedException e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrChat.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }).start();
    }

    public String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }

    @Override
    public void onDestroy()
    {
        closeThread();
        super.onDestroy();
    }
    public void sendMessage()
    {
        String strMessage = editMessage.getText().toString();
        if (!strMessage.equals(""))
        {
            ServiceManager.onSendMessage(prefClass.getSavedStringPreference(SharedPrefClass.USER_ID),strMessage,this);
        }
        editMessage.setText("");
    }
    public void closeThread()
    {
        isLoad = false;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgChatSend:
                makeSound();
                sendMessage();
                break;
        }
    }
    public void makeSound()
    {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public class MessageHolder
    {
        public TextView txtMessage;
        public TextView txtTime;
    }
}
