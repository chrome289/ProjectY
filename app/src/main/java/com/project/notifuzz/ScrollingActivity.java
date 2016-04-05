package com.project.notifuzz;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;

import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Bitmap> appIcon=new ArrayList<>();
    private ArrayList<String> appName= new ArrayList<>();
    private ArrayList<String> notiHead= new ArrayList<>();
    private ArrayList<String> notiContent= new ArrayList<>();
    private ArrayList<String> id= new ArrayList<>();
   private ArrayList<PendingIntent> pendingIntents=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Notifuzz");
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        try {
                            pendingIntents.get(position).send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                })
        );
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
               appIcon.remove(viewHolder.getAdapterPosition());
                notiContent.remove(viewHolder.getAdapterPosition());
                notiHead.remove(viewHolder.getAdapterPosition());
                pendingIntents.remove(viewHolder.getAdapterPosition());
                id.remove(viewHolder.getAdapterPosition());
                appName.remove(viewHolder.getAdapterPosition());
                recyclerViewAdapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        Button button = (Button) this.findViewById(R.id.button);
        button.setOnClickListener(this);

        if (!Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("Message", "recieved");
            if(id.contains(intent.getStringExtra("id"))){
                PackageManager packageManager = getApplicationContext().getPackageManager();
                String app = null;
                try {
                    app = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(intent.getStringExtra("package"), PackageManager.GET_META_DATA));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                appName.set((id.indexOf(intent.getStringExtra("id"))),app);
                notiHead.set((id.indexOf(intent.getStringExtra("id"))), intent.getStringExtra("title"));
                notiContent.set((id.indexOf(intent.getStringExtra("id"))), intent.getStringExtra("text"));

                Bitmap icon = null;
                try {
                    icon = ((BitmapDrawable) getPackageManager().getApplicationIcon(intent.getStringExtra("package"))).getBitmap();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                appIcon.set((id.indexOf(intent.getStringExtra("id"))), icon);
                id.set((id.indexOf(intent.getStringExtra("id"))), intent.getStringExtra("id"));
                pendingIntents.set((id.indexOf(intent.getStringExtra("id"))), (PendingIntent) intent.getParcelableExtra("intent"));
            }else {
                PackageManager packageManager = getApplicationContext().getPackageManager();
                String app = null;
                try {
                    app = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(intent.getStringExtra("package"), PackageManager.GET_META_DATA));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                appName.add(app);
                notiHead.add(intent.getStringExtra("title"));
                notiContent.add(intent.getStringExtra("text"));

                Bitmap icon = null;
                try {
                    icon = ((BitmapDrawable) getPackageManager().getApplicationIcon(intent.getStringExtra("package"))).getBitmap();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                appIcon.add(icon);
                id.add(intent.getStringExtra("id"));
                pendingIntents.add((PendingIntent) intent.getParcelableExtra("intent"));
            }
            recyclerViewAdapter.updateList(appIcon,appName,notiHead,notiContent,id,pendingIntents);
            recyclerViewAdapter.notifyDataSetChanged();
            /*String temp = intent.getStringExtra("notification_event") + "\n" + txtView.getText();
            txtView.setText(temp);*/
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
                ncomp.setContentTitle("My Notification");
                ncomp.setContentText("Notification Listener Service Example");
                ncomp.setTicker("Notification Listener Service Example");
                ncomp.setSmallIcon(R.mipmap.ic_launcher);
                ncomp.setAutoCancel(true);
                nManager.notify((int) System.currentTimeMillis(), ncomp.build());
                break;
        }
    }
}
