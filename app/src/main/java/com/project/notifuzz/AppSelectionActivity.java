package com.project.notifuzz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppSelectionActivity extends AppCompatActivity implements View.OnClickListener {

    ListView listView;
    ListViewAdapter listViewAdapter;
    TabHost host;
    GestureDetectorCompat gDetector;
    View pview;
    int pre = 0;
    Context context;
    String TAG = "Message";
    GridViewAdapter gridViewAdapter;
    public static ArrayList<String> tileText = new ArrayList<>();
    public static ArrayList<String> tileColor = new ArrayList<>();
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button3) {
            final ColorPicker colorPicker = new ColorPicker(AppSelectionActivity.this, 200, 200, 200);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Add Categories");
            alert.setMessage("Enter the Name");
            final EditText editText = new EditText(this);
            alert.setView(editText);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i2) {
                    Log.v(TAG, "Add this shit up");
                }
            });

            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.v(TAG, "Cut this shit out");
                }
            });

            final AlertDialog alertDialog = alert.create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Set<String> stringSet3;
                    stringSet3 = sharedPreferences.getStringSet("categories", new HashSet<String>());
                    Log.v("Message", String.valueOf(editText.getText()));
                    if (!stringSet3.contains(String.valueOf(editText.getText()))) {
                        colorPicker.show();
                        Button button = (Button) colorPicker.findViewById(R.id.okColorButton);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Set<String> stringSet2;
                                stringSet2 = sharedPreferences.getStringSet("cat_color", new HashSet<String>());
                                stringSet2.add(String.valueOf(Color.rgb(colorPicker.getRed(), colorPicker.getGreen(), colorPicker.getBlue())));
                                editor.putStringSet("cat_color", stringSet2);
                                Log.v(TAG, String.valueOf(Color.rgb(colorPicker.getRed(), colorPicker.getGreen(), colorPicker.getBlue())));
                                editor.commit();

                                Set<String> stringSet;
                                stringSet = sharedPreferences.getStringSet("categories", new HashSet<String>());
                                stringSet.add(String.valueOf(editText.getText()));
                                editor.putStringSet("categories", stringSet);
                                editor.commit();

                                tileText.add(String.valueOf(editText.getText()));
                                tileColor.add(String.valueOf(Color.rgb(colorPicker.getRed(), colorPicker.getGreen(), colorPicker.getBlue())));

                                gridViewAdapter.notifyDataSetChanged();
                                colorPicker.cancel();
                                alertDialog.dismiss();
                            }
                        });
                    }
                    else{
                        TextView messageView = (TextView)alertDialog.findViewById(android.R.id.message);
                        messageView.setText("Category already Present..\nUse different name");
                    }
                }
            });
        }
    }


    public class AppDet {
        String app, name,category;
        Boolean appIsEnabled;
        Bitmap bitmaps;
    }

    public class AppComparator implements Comparator<AppDet> {
        @Override
        public int compare(AppDet o1, AppDet o2) {
            return o1.name.compareToIgnoreCase(o2.name);
        }
    }

    public static ArrayList<AppDet> appDet = new ArrayList<>();

    public void switchTabs(boolean direction) {
        if (direction) // true = move left
        {
            if (host.getCurrentTab() != 0)
                host.setCurrentTab(host.getCurrentTab() - 1);
        } else // move right
        {
            if (host.getCurrentTab() != (host.getTabWidget().getTabCount() - 1))
                host.setCurrentTab(host.getCurrentTab() + 1);
        }
    }

    // tab animation
    public Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(300);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    public Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(300);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    public Animation inFromLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(300);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    public Animation outToRightAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(300);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);
        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        if(!sharedPreferences.contains("categories")) {
            editor.putStringSet("categories", null);
            Log.v(TAG,"fdgdfgdfgdgdfgdfg");
        }
        if(!sharedPreferences.contains("cat_color")){
            editor.putStringSet("cat_color",null);
            Log.v(TAG,"fdgdfgdfgdgdfgdfg");
        }
        editor.commit();

        gDetector = new GestureDetectorCompat(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                Log.i("motion", "onFling has been called!");
                final int SWIPE_MIN_DISTANCE = 120;
                final int SWIPE_MAX_OFF_PATH = 250;
                final int SWIPE_THRESHOLD_VELOCITY = 200;
                try {
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                        return false;
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                            && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        Log.i("motion", "Right to Left");
                        switchTabs(false);
                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                            && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                        Log.i("motion", "Left to Right");
                        switchTabs(true);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }
        });

        //tab host setup
        if (host == null) {
            host = (TabHost) findViewById(R.id.tabHost);
            host.setup();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                host.setNestedScrollingEnabled(false);
            }

            TabHost.TabSpec tabSpec = host.newTabSpec("Applications");
            tabSpec.setContent(R.id.linearLayout);
            tabSpec.setIndicator("Applications");
            host.addTab(tabSpec);

            tabSpec = host.newTabSpec("Categories");
            tabSpec.setContent(R.id.linearLayout2);
            tabSpec.setIndicator("Categories");
            host.addTab(tabSpec);
        }
        //swipe listener
        host.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gDetector.onTouchEvent(event);
            }
        });

        //tab change listener
        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            public void onTabChanged(String tabId) {
                View cur = null;
                //select animation based on direction
                if (pre < host.getCurrentTab()) {
                    Log.i("selected tab ", tabId);
                    cur = host.getCurrentView();
                    if (pview != null) {
                        cur.setAnimation(inFromRightAnimation());
                        pview.setAnimation(outToLeftAnimation());
                    } else {//when tab is clicked not swiped
                        Log.v(TAG, "tag no " + pre + "  " + host.getCurrentTab());
                        if (pre > host.getCurrentTab()) {
                            cur.setAnimation(inFromLeftAnimation());
                        } else {
                            cur.setAnimation(inFromRightAnimation());
                        }
                    }
                } else if (pre > host.getCurrentTab()) {
                    Log.i("selected tab ", tabId);
                    cur = host.getCurrentView();
                    if (pview != null) {
                        cur.setAnimation(inFromLeftAnimation());
                        pview.setAnimation(outToRightAnimation());
                    } else {//when tab is clicked not swiped
                        Log.v(TAG, "tag no " + pre + "  " + host.getCurrentTab());
                        if (pre > host.getCurrentTab()) {
                            cur.setAnimation(inFromLeftAnimation());
                        } else {
                            cur.setAnimation(inFromRightAnimation());
                        }
                    }
                }
                pview = cur;
                pre = host.getCurrentTab();
            }
        });


        appDet = new ArrayList<>();

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.button3);
        floatingActionButton.setOnClickListener(this);
        Switch switc = (Switch) findViewById(R.id.switch2);
        switc.setChecked(sharedPreferences.getBoolean("isEnabled", false));
        switc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean temp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("isEnabled", true);
                editor.putBoolean("isEnabled", !temp);
                editor.commit();
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listViewAdapter = new ListViewAdapter(this);
        listView.setAdapter(listViewAdapter);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //layoutmanager for recyclerview
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        //onClick listener
        gridViewAdapter = new GridViewAdapter();

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(gridViewAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(gridViewAdapter);

        Set<String> stringSet =sharedPreferences.getStringSet("categories",new HashSet<String>());
        Set<String> stringSet2 =sharedPreferences.getStringSet("cat_color", new HashSet<String>());
        if(!stringSet.contains("General")){
            stringSet.add("General");
            stringSet2.add("-3618616");
            editor.putStringSet("categories",stringSet);
            editor.putStringSet("cat_color",stringSet2);
            editor.commit();
        }

        Log.v(TAG, String.valueOf(stringSet.size()));
        tileColor.clear();
        tileText.clear();
        for (int i = 0; i < stringSet.size(); i++) {
            Log.v(TAG,stringSet.toArray()[i]+"     "+stringSet2.toArray()[i]);
            tileText.add((String) stringSet.toArray()[i]);
            tileColor.add((String) stringSet2.toArray()[i]);
        }

        gridViewAdapter.notifyDataSetChanged();
        //set adapter

        //get complete app list
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            AppDet temp = new AppDet();
            temp.app = packageInfo.packageName;
            if (!sharedPreferences.contains(packageInfo.packageName)) {
                editor.putBoolean(packageInfo.packageName, true);
                editor.commit();
            }
            if (!sharedPreferences.contains("cat_"+packageInfo.packageName)) {
                editor.putString("cat_"+packageInfo.packageName,"");
                editor.commit();
            }
            temp.appIsEnabled = sharedPreferences.getBoolean(packageInfo.packageName, true);
            temp.category=sharedPreferences.getString("cat_"+packageInfo.packageName,"");

            try {
                ApplicationInfo app = pm.getApplicationInfo(packageInfo.packageName, 0);
                temp.name = String.valueOf(pm.getApplicationLabel(app));
                Log.v("fd", "dfd");
                temp.bitmaps = ((BitmapDrawable) pm.getApplicationIcon(app)).getBitmap();
            } catch (Exception e) {
                e.printStackTrace();
            }
            appDet.add(temp);
        }
        Collections.sort(appDet, new AppComparator());
        listViewAdapter.notifyDataSetChanged();
    }
}