package com.project.notifuzz;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class ScrollingActivity extends AppCompatActivity implements View.OnClickListener {

    //arraylist for saving notification data
    private RecyclerParentViewAdapter recyclerParentViewAdapter;
    public static Context context;
    RecyclerView recyclerParentView;
    static TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        context = this;

        //initializing preferences
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Notifuzz");
        setSupportActionBar(toolbar);
        textView= (TextView)findViewById(R.id.textView);

        recyclerParentView = (RecyclerView) findViewById(R.id.recyclerView);

        //layoutmanager for recyclerview
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerParentView.setLayoutManager(mLayoutManager);

        recyclerParentViewAdapter = new RecyclerParentViewAdapter(NotificationService.categories, NotificationService.recyclerViewAdapters, NotificationService.notificationView);
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

        update();
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
            update();

            for (int i = 0; i < NotificationService.recyclerViewAdapters.size(); i++)
                NotificationService.recyclerViewAdapters.get(i).notifyDataSetChanged();

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
                ncomp.setContentText("Testing " + System.currentTimeMillis() / 1000);
                ncomp.setTicker("1...2...3...");
                ncomp.setSmallIcon(R.mipmap.signs);
                ncomp.setAutoCancel(true);
                nManager.notify((int) System.currentTimeMillis(), ncomp.build());
                break;
            case R.id.button2:
                startActivity(new Intent(this, AppSelectionActivity.class));
        }
    }

    public static void update() {
        int counter = 0;
        for (int i = 0; i < NotificationService.notificationView.size(); i++)
            counter += NotificationService.notificationView.get(i).size();
        textView.setText(counter + " Notifications");
    }
}
