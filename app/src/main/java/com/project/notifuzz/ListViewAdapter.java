package com.project.notifuzz;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
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

            ArrayAdapter<String>arrayAdapter=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,AppSelectionActivity.tileText);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            final Spinner spinner=(Spinner)convertView.findViewById(R.id.spinner);
            spinner.setAdapter(arrayAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.v("dsf","sdfsdf");
                    spinner.setSelection(i);
                    AppSelectionActivity.editor.putString("cat_"+AppSelectionActivity.appDet.get(position).app,AppSelectionActivity.tileText.get(i)).commit();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Log.v("dsf","sdfsdf");
                }
            });
            String temp=AppSelectionActivity.appDet.get(position).category;
            Log.v("Message2",temp);
            if(AppSelectionActivity.tileText.indexOf(temp)!=-1)
                spinner.setSelection(AppSelectionActivity.tileText.indexOf(temp));
            else
                spinner.setSelection(0);

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