package com.project.notifuzz;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NotificationService extends NotificationListenerService {

    private Context context;
    int counter = 0;

    public static ArrayList<String> categories = new ArrayList<>();
    public static ArrayList<RecyclerViewAdapter> recyclerViewAdapters = new ArrayList<>();
    public static ArrayList<ArrayList<NotificationView>> notificationView = new ArrayList<>();

    private PendingIntent pen;
    private String timeS;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> stringSet = sharedPreferences.getStringSet("categories", new HashSet<String>());
        Set<String> stringSet2 = sharedPreferences.getStringSet("cat_color", new HashSet<String>());
        if (!stringSet.contains("General")) {
            stringSet.add("General");
            stringSet2.add("-3618616");
            editor.putStringSet("categories", stringSet);
            editor.putStringSet("cat_color", stringSet2);
            editor.commit();
        }
        if (categories.size() == 0) {
            for (int i = 0; i < stringSet.size(); i++) {
                Log.v("Message", stringSet.toArray()[i] + "     " + stringSet2.toArray()[i]);
                categories.add((String) stringSet.toArray()[i]);
                notificationView.add(new ArrayList<NotificationView>());
                recyclerViewAdapters.add(new RecyclerViewAdapter(notificationView.get(i)));
            }
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //check if global enabled
        Log.v("dfsdf", counter + "");
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isEnabled", false)) {
            String pack = sbn.getPackageName();
            //check if app enabled
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(pack, true)) {
                NotificationView temp = new NotificationView();
                counter++;
                //get data of notification and bundle it into an intent
                Bundle extras = sbn.getNotification().extras;
                int priortyS = sbn.getNotification().priority;
                String title = extras.getString("android.title");
                String text = extras.getCharSequence("android.text").toString();
                String idS = sbn.getId() + sbn.getPackageName();

                String str = PreferenceManager.getDefaultSharedPreferences(this).getString("cat_" + sbn.getPackageName(), "General");
                int pos = categories.indexOf(str);

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

                if (NotificationView.searchID(notificationView.get(pos), idS)) {
                    int pos2 = NotificationView.indexOfID(notificationView.get(pos), idS);
                    //get data from recieved intent and assign
                    PackageManager packageManager = getApplicationContext().getPackageManager();
                    String app = null;
                    try {
                        app = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(pack, PackageManager.GET_META_DATA));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    notificationView.get(pos).get(pos2).appName = app;
                    notificationView.get(pos).get(pos2).notiHead = title;
                    notificationView.get(pos).get(pos2).notiContent = text;


                    try {
                        notificationView.get(pos).get(pos2).appIcon = ((BitmapDrawable) getPackageManager().getApplicationIcon(pack)).getBitmap();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    notificationView.get(pos).get(pos2).id = idS;
                    notificationView.get(pos).get(pos2).pendingIntents = pen;
                    notificationView.get(pos).get(pos2).time = timeS;
                    notificationView.get(pos).get(pos2).priorty = priortyS;

                } else {
                    PackageManager packageManager = getApplicationContext().getPackageManager();
                    String app = null;
                    try {
                        app = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(pack, PackageManager.GET_META_DATA));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    temp.appName = app;
                    temp.notiHead = title;
                    temp.notiContent = text;

                    try {
                        temp.appIcon = ((BitmapDrawable) getPackageManager().getApplicationIcon(pack)).getBitmap();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    temp.id = idS;
                    temp.pendingIntents = pen;
                    temp.priorty = priortyS;
                    temp.time = timeS;
                    notificationView.get(pos).add(temp);
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