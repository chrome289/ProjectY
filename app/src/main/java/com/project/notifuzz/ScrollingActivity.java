package com.project.notifuzz;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.TextView;

public class ScrollingActivity extends AppCompatActivity implements View.OnClickListener {

    //arraylist for saving notification data
    private RecyclerViewAdapter recyclerViewAdapter;

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
                            if (NotificationService.pendingIntents.get(position) != null) {
                                //pendingIntent used
                                NotificationService.pendingIntents.get(position).send();
                            }
                            //remove entry from recyclerview
                            NotificationService.appIcon.remove(position);
                            NotificationService.notiContent.remove(position);
                            NotificationService.notiHead.remove(position);
                            NotificationService.pendingIntents.remove(position);
                            NotificationService.id.remove(position);
                            NotificationService.appName.remove(position);
                            NotificationService.time.remove(position);
                            NotificationService.priorty.remove(position);
                            recyclerViewAdapter.notifyDataSetChanged();
                            TextView textView = (TextView) findViewById(R.id.textView);
                            textView.setText(NotificationService.appIcon.size() + " Notifications");

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
                NotificationService.appIcon.remove(viewHolder.getAdapterPosition());
                NotificationService.notiContent.remove(viewHolder.getAdapterPosition());
                NotificationService.notiHead.remove(viewHolder.getAdapterPosition());
                NotificationService.pendingIntents.remove(viewHolder.getAdapterPosition());
                NotificationService.id.remove(viewHolder.getAdapterPosition());
                NotificationService.appName.remove(viewHolder.getAdapterPosition());
                NotificationService.time.remove(viewHolder.getAdapterPosition());
                NotificationService.priorty.remove(viewHolder.getAdapterPosition());
                recyclerViewAdapter.notifyDataSetChanged();
                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText(NotificationService.appIcon.size() + " Notifications");

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
            startActivityForResult(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), 0);
        }

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(NotificationService.appIcon.size() + " Notifications");
        recyclerViewAdapter.updateList(NotificationService.appIcon, NotificationService.appName, NotificationService.notiHead, NotificationService.notiContent, NotificationService.id, NotificationService.pendingIntents, NotificationService.time, NotificationService.priorty);
        recyclerViewAdapter.notifyDataSetChanged();
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
            textView.setText(NotificationService.appIcon.size() + " Notifications");
            recyclerViewAdapter.updateList(NotificationService.appIcon, NotificationService.appName, NotificationService.notiHead, NotificationService.notiContent, NotificationService.id, NotificationService.pendingIntents, NotificationService.time, NotificationService.priorty);
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
                ncomp.setContentTitle("Notifuzz");
                ncomp.setContentText("Testing");
                ncomp.setTicker("1...2...3...");
                ncomp.setSmallIcon(R.mipmap.signs);
                ncomp.setAutoCancel(true);
                nManager.notify((int) System.currentTimeMillis(), ncomp.build());
                break;
            case R.id.button2:
                startActivity(new Intent(this, AppSelectionActivity.class));
        }
    }
}
