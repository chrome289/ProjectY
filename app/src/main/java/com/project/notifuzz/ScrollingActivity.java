package com.project.notifuzz;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity implements View.OnClickListener {

    //arraylist for saving notification data
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Bitmap> appIcon = new ArrayList<>();
    private ArrayList<String> appName = new ArrayList<>();
    private ArrayList<String> notiHead = new ArrayList<>();
    private ArrayList<String> notiContent = new ArrayList<>();
    private ArrayList<String> time = new ArrayList<>();
    private ArrayList<String> id = new ArrayList<>();
    private ArrayList<PendingIntent> pendingIntents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        //initializing preferences
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Notifuzz");
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //layoutmanager for recyclerview
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        //onClick listener
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        try {
                            if (pendingIntents.get(position) != null) {
                                //pendingIntent used
                                pendingIntents.get(position).send();
                            }
                            //remove entry from recyclerview
                            appIcon.remove(position);
                            notiContent.remove(position);
                            notiHead.remove(position);
                            pendingIntents.remove(position);
                            id.remove(position);
                            appName.remove(position);
                            time.remove(position);
                            recyclerViewAdapter.notifyDataSetChanged();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                })
        );
        //ItemTouchHelper callback for swipe gesture
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            //draw colored rectangle during swipe gesture
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;

                    Paint p = new Paint();
                    p.setColor(Color.rgb(224, 160, 150));
                    if (dX > 0)
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), p);
                    else
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            //clear entry from recyclerview on swipeCompleted
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                appIcon.remove(viewHolder.getAdapterPosition());
                notiContent.remove(viewHolder.getAdapterPosition());
                notiHead.remove(viewHolder.getAdapterPosition());
                pendingIntents.remove(viewHolder.getAdapterPosition());
                id.remove(viewHolder.getAdapterPosition());
                appName.remove(viewHolder.getAdapterPosition());
                time.remove(viewHolder.getAdapterPosition());
                recyclerViewAdapter.notifyDataSetChanged();
            }
        };

        //assign ItemTouchCallback to recyclerview
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerViewAdapter = new RecyclerViewAdapter();
        //set adapter
        recyclerView.setAdapter(recyclerViewAdapter);

        FloatingActionButton button = (FloatingActionButton) this.findViewById(R.id.button);
        button.setOnClickListener(this);
        button = (FloatingActionButton) this.findViewById(R.id.button2);
        button.setOnClickListener(this);

        //ask user for notification access permission
        if (!Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }

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
            //check if earlier version of notification exists
            if (id.contains(intent.getStringExtra("id"))) {
                //get data from recieved intent and assign
                PackageManager packageManager = getApplicationContext().getPackageManager();
                String app = null;
                try {
                    app = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(intent.getStringExtra("package"), PackageManager.GET_META_DATA));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                appName.set((id.indexOf(intent.getStringExtra("id"))), app);
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
                time.set((id.indexOf(intent.getStringExtra("id"))), intent.getStringExtra("time"));
                pendingIntents.set((id.indexOf(intent.getStringExtra("id"))), (PendingIntent) intent.getParcelableExtra("intent"));
            } else {
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
                time.add(intent.getStringExtra("time"));
            }
            Log.v("dfdf", intent.getStringExtra("time"));
            //update recyclerview
            recyclerViewAdapter.updateList(appIcon, appName, notiHead, notiContent, id, pendingIntents, time);
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
            case R.id.button2:
                startActivity(new Intent(this,AppSelectionActivity.class));
        }
    }
}
