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

import java.util.ArrayList;

public class NotificationService extends NotificationListenerService {

    private Context context;
    int counter = 0;

    public static ArrayList<Bitmap> appIcon = new ArrayList<>();
    public static ArrayList<String> appName = new ArrayList<>();
    public static ArrayList<String> notiHead = new ArrayList<>();
    public static ArrayList<String> notiContent = new ArrayList<>();
    public static ArrayList<String> time = new ArrayList<>();
    public static ArrayList<String> id = new ArrayList<>();
    public static ArrayList<PendingIntent> pendingIntents = new ArrayList<>();
    public static ArrayList<Integer> priorty = new ArrayList<>();
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
                counter++;
                //get data of notification and bundle it into an intent
                Bundle extras = sbn.getNotification().extras;
                int priortyS = sbn.getNotification().priority;
                String title = extras.getString("android.title");
                String text = extras.getCharSequence("android.text").toString();
                String idS = sbn.getId() + sbn.getPackageName();
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

                if (id.contains(idS)) {
                    //get data from recieved intent and assign
                    PackageManager packageManager = getApplicationContext().getPackageManager();
                    String app = null;
                    try {
                        app = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(pack, PackageManager.GET_META_DATA));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    appName.set((id.indexOf(idS)), app);
                    notiHead.set((id.indexOf(idS)), title);
                    notiContent.set((id.indexOf(idS)), text);

                    Bitmap icon = null;
                    try {
                        icon = ((BitmapDrawable) getPackageManager().getApplicationIcon(pack)).getBitmap();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    appIcon.set((id.indexOf(idS)), icon);
                    id.set((id.indexOf(idS)), idS);
                    time.set((id.indexOf(idS)), timeS);
                    pendingIntents.set((id.indexOf(idS)), pen);
                    priorty.add(priortyS);
                } else {
                    PackageManager packageManager = getApplicationContext().getPackageManager();
                    String app = null;
                    try {
                        app = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(pack, PackageManager.GET_META_DATA));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    appName.add(app);
                    notiHead.add(title);
                    notiContent.add(text);

                    Bitmap icon = null;
                    try {
                        icon = ((BitmapDrawable) getPackageManager().getApplicationIcon(pack)).getBitmap();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    appIcon.add(icon);
                    id.add(idS);
                    pendingIntents.add(pen);
                    priorty.add(priortyS);
                    time.add(timeS);
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