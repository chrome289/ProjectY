package com.project.notifuzz;

import android.app.PendingIntent;
import android.graphics.Bitmap;

import java.util.ArrayList;

public class NotificationView {
        public Bitmap appIcon;
        public String appName, notiHead, notiContent, id, time;
        public PendingIntent pendingIntents;
        public int priorty;

        public static boolean searchID(ArrayList<NotificationView> notificationView,String ID) {
            for (int i = 0; i < notificationView.size(); i++) {
                if (notificationView.get(i).id.equals(ID))
                    return true;
            }
            return false;
        }

        public static int indexOfID(ArrayList<NotificationView> notificationView,String ID) {
            for (int i = 0; i < notificationView.size(); i++) {
                if (notificationView.get(i).id.equals(ID))
                    return i;
            }
            return -1;
        }
    }