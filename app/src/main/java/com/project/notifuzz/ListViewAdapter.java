package com.project.notifuzz;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


public class ListViewAdapter extends BaseAdapter {
    private Activity context;

    //constructor
    public ListViewAdapter(Activity context) {
        super();
        this.context = context;
    }

    public int getCount() {
        return AppSelectionActivity.appDet.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            //inflate view for entry and set data
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.app_listview_card, null);
            ImageView imageView=(ImageView)convertView.findViewById(R.id.imageView);
            imageView.setImageBitmap(AppSelectionActivity.appDet.get(position).bitmaps);
            TextView t = (TextView) convertView.findViewById(R.id.textView3);
            t.setText(AppSelectionActivity.appDet.get(position).app+"");
            t = (TextView) convertView.findViewById(R.id.textView2);
            t.setText(AppSelectionActivity.appDet.get(position).name + "");
            Switch switc=(Switch)convertView.findViewById(R.id.switch1);
            //onClick listener for 'enabled' switch
            switc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("we are ","one");
                    // update app preference
                    boolean temp= PreferenceManager.getDefaultSharedPreferences(context).getBoolean(AppSelectionActivity.appDet.get(position).app,true);
                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
                    editor.putBoolean(AppSelectionActivity.appDet.get(position).app,!temp);
                    editor.commit();
                }
            });
            switc.setChecked(AppSelectionActivity.appDet.get(position).appIsEnabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}