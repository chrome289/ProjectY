package com.project.notifuzz;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class NotificationService extends NotificationListenerService {

    private Context context;
    int counter = 0;

    private PendingIntent pen;
    private String timeS;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //check if global enabled
        Log.v("dfsdf", counter + "");
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isEnabled", false)) {
            String pack = sbn.getPackageName();
            //check if app enabled
            if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(pack,true)) {
                NotificationView temp=new NotificationView();
                counter++;
                //get data of notification and bundle it into an intent
                Bundle extras = sbn.getNotification().extras;
                int priortyS = sbn.getNotification().priority;
                String title = extras.getString("android.title");
                String text = extras.getCharSequence("android.text").toString();
                String idS = sbn.getId() + sbn.getPackageName();

                String str=PreferenceManager.getDefaultSharedPreferences(this).getString("cat_"+sbn.getPackageName(),"General");
                int pos=ScrollingActivity.categories.indexOf(str);

                //remove notification via key if available
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cancelNotification(sbn.getKey());
                } else {
                    cancelNotification(pack, sbn.getTag(), sbn.getId());
                }

                if (sbn.getNotification().contentIntent != null)
                    pen = sbn.getNotification().contentIntent;
                else
                    pen = null;
                timeS = sbn.getPostTime() + "";

                if (NotificationView.searchID(ScrollingActivity.notificationView.get(pos),idS)) {
                    //get data from recieved intent and assign
                    PackageManager packageManager = getApplicationContext().getPackageManager();
                    String app = null;
                    try {
                        app = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(pack, PackageManager.GET_META_DATA));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    ScrollingActivity.notificationView.get(pos).get(NotificationView.indexOfID(ScrollingActivity.notificationView.get(pos),idS)).appName=app;
                    ScrollingActivity.notificationView.get(pos).get(NotificationView.indexOfID(ScrollingActivity.notificationView.get(pos),idS)).notiHead=title;
                    ScrollingActivity.notificationView.get(pos).get(NotificationView.indexOfID(ScrollingActivity.notificationView.get(pos),idS)).notiContent=text;


                    Bitmap icon = null;
                    try {
                        icon = ((BitmapDrawable) getPackageManager().getApplicationIcon(pack)).getBitmap();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    ScrollingActivity.notificationView.get(pos).get(NotificationView.indexOfID(ScrollingActivity.notificationView.get(pos),idS)).appIcon=icon;
                    ScrollingActivity.notificationView.get(pos).get(NotificationView.indexOfID(ScrollingActivity.notificationView.get(pos),idS)).id=idS;
                    ScrollingActivity.notificationView.get(pos).get(NotificationView.indexOfID(ScrollingActivity.notificationView.get(pos),idS)).pendingIntents=pen;
                    ScrollingActivity.notificationView.get(pos).get(NotificationView.indexOfID(ScrollingActivity.notificationView.get(pos),idS)).time=timeS;
                    ScrollingActivity.notificationView.get(pos).get(NotificationView.indexOfID(ScrollingActivity.notificationView.get(pos),idS)).priorty=priortyS;

                } else {
                    PackageManager packageManager = getApplicationContext().getPackageManager();
                    String app = null;
                    try {
                        app = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(pack, PackageManager.GET_META_DATA));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    temp.appName=app;
                    temp.notiHead=title;
                    temp.notiContent=text;

                    Bitmap icon = null;
                    try {
                        icon = ((BitmapDrawable) getPackageManager().getApplicationIcon(pack)).getBitmap();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    temp.appIcon=icon;
                    temp.id=idS;
                    temp.pendingIntents=pen;
                    temp.priorty=priortyS;
                    temp.time=timeS;
                    ScrollingActivity.notificationView.get(pos).add(temp);
                }

                //send broadcast to activity for gui update

                Intent msgrcv = new Intent("Msg");
                LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
            }
        }
    }

    //notification removed callback
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }
}