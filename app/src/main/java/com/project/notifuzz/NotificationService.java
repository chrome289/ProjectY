package com.project.notifuzz;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class NotificationService extends NotificationListenerService {

    private Context context;

    @Override

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override

    public void onNotificationPosted(StatusBarNotification sbn) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isEnabled", false)) {
            String pack = sbn.getPackageName();
            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString("android.title");
            String text = extras.getCharSequence("android.text").toString();
            String id = sbn.getId() + sbn.getPackageName();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cancelNotification(sbn.getKey());
            } else {
                cancelNotification(pack, sbn.getTag(), sbn.getId());
            }

            Log.i("Package", pack);
            Log.i("Title", title);
            Log.i("Text", text);

            Intent msgrcv = new Intent("Msg");
            msgrcv.putExtra("package", pack);
            msgrcv.putExtra("title", title);
            msgrcv.putExtra("text", text);
            msgrcv.putExtra("id", id);
            if (sbn.getNotification().contentIntent != null)
                msgrcv.putExtra("intent", sbn.getNotification().contentIntent);
            else
                msgrcv.putExtra("intent", "null");
            msgrcv.putExtra("time",sbn.getPostTime()+"");

            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
        }
    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }
}