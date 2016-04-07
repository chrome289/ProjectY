package com.project.notifuzz;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppSelectionActivity extends AppCompatActivity {

    ListView listView;
    ListViewAdapter listViewAdapter;

    public class AppDet{
        String app,name;
        Boolean appIsEnabled;
        Bitmap bitmaps;
    }
    public class AppComparator implements Comparator<AppDet> {
        @Override
        public int compare(AppDet o1, AppDet o2) {
            return o1.name.compareToIgnoreCase(o2.name);
        }
    }

    public static ArrayList<AppDet> appDet=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);

        appDet=new ArrayList<>();
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor=sharedPreferences.edit();

        Switch switc=(Switch)findViewById(R.id.switch2);
        switc.setChecked(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isEnabled", false));
        switc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean temp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("isEnabled", true);
                editor.putBoolean("isEnabled",!temp);
                editor.commit();
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listViewAdapter = new ListViewAdapter(this);
        listView.setAdapter(listViewAdapter);

        //get complete app list
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            AppDet temp=new AppDet();
            temp.app=packageInfo.packageName;
            if(!sharedPreferences.contains(packageInfo.packageName)){
                editor.putBoolean(packageInfo.packageName,true);
                editor.commit();
            }
            temp.appIsEnabled=PreferenceManager.getDefaultSharedPreferences(this).getBoolean(packageInfo.packageName,true);
            try {
                ApplicationInfo app = pm.getApplicationInfo(packageInfo.packageName, 0);
                temp.name=String.valueOf(pm.getApplicationLabel(app));
                Log.v("fd","dfd");
                temp.bitmaps=((BitmapDrawable) pm.getApplicationIcon(app)).getBitmap();
            } catch (Exception e) {
                e.printStackTrace();
            }
            appDet.add(temp);
        }
        Collections.sort(appDet,new AppComparator());
        listViewAdapter.notifyDataSetChanged();
    }
}