package com.project.notifuzz;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ScrollingActivity extends AppCompatActivity implements View.OnClickListener {

    //arraylist for saving notification data
    public static ArrayList<ArrayList<NotificationView>> notificationView=new ArrayList<>();
    private RecyclerParentViewAdapter recyclerParentViewAdapter;
    public static Context context;
    RecyclerView recyclerParentView;
    public static ArrayList<String> categories=new ArrayList<>();
    public static ArrayList<RecyclerViewAdapter> recyclerViewAdapters=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        context=this;

        //initializing preferences
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Notifuzz");
        setSupportActionBar(toolbar);

        recyclerParentView = (RecyclerView) findViewById(R.id.recyclerView);

        //layoutmanager for recyclerview
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerParentView.setLayoutManager(mLayoutManager);

        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Set<String> stringSet =sharedPreferences.getStringSet("categories",new HashSet<String>());
        Set<String> stringSet2 =sharedPreferences.getStringSet("cat_color", new HashSet<String>());
        if(!stringSet.contains("General")){
            stringSet.add("General");
            stringSet2.add("-3618616");
            editor.putStringSet("categories",stringSet);
            editor.putStringSet("cat_color",stringSet2);
            editor.commit();
        }

        if(categories.size()==0) {
            for (int i = 0; i < stringSet.size(); i++) {
                Log.v("Message", stringSet.toArray()[i] + "     " + stringSet2.toArray()[i]);
                categories.add((String) stringSet.toArray()[i]);
                notificationView.add(new ArrayList<NotificationView>());
                recyclerViewAdapters.add(new RecyclerViewAdapter(notificationView.get(i)));
            }
        }

        recyclerParentViewAdapter=new RecyclerParentViewAdapter(categories,recyclerViewAdapters,notificationView);
        recyclerParentView.setAdapter(recyclerParentViewAdapter);
        recyclerParentViewAdapter.notifyDataSetChanged();

        FloatingActionButton button = (FloatingActionButton) this.findViewById(R.id.button);
        button.setOnClickListener(this);
        button = (FloatingActionButton) this.findViewById(R.id.button2);
        button.setOnClickListener(this);

        //ask user for notification access permission
        if (!Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
            startActivityForResult(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), 0);
        }

        TextView textView = (TextView) findViewById(R.id.textView);
        int counter=0;
        for(int i=0;i<notificationView.size();i++)
            counter+=notificationView.get(i).size();
        textView.setText(counter + " Notifications");
        //RecyclerViewAdapter.updateList(NotificationService.notificationView);
        //recyclerViewAdapter.notifyDataSetChanged();
        //register broadcast reciever from service
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //settings window
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, PreferenceActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //recieved broadcast from service
            Log.v("Message", "recieved");
            //update recyclerview
            TextView textView = (TextView) findViewById(R.id.textView);
            int counter=0;
            for(int i=0;i<notificationView.size();i++)
                counter+=notificationView.get(i).size();
            textView.setText(counter + " Notifications");

            for(int i=0;i<recyclerViewAdapters.size();i++)
                recyclerViewAdapters.get(i).notifyDataSetChanged();

            recyclerParentViewAdapter.notifyDataSetChanged();

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
                ncomp.setContentTitle("Notifuzz");
                ncomp.setContentText("Testing "+System.currentTimeMillis()/1000);
                ncomp.setTicker("1...2...3...");
                ncomp.setSmallIcon(R.mipmap.signs);
                ncomp.setAutoCancel(true);
                nManager.notify((int) System.currentTimeMillis(), ncomp.build());
                break;
            case R.id.button2:
                startActivity(new Intent(this, AppSelectionActivity.class));
        }
    }

    public  void remove(){
        TextView textView = (TextView)findViewById(R.id.textView);
        int counter=0;
        for(int i=0;i<notificationView.size();i++)
            counter+=notificationView.get(i).size();
        textView.setText(counter + " Notifications");
    }
}
