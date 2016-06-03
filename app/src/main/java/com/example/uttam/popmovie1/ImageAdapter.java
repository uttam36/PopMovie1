package com.example.uttam.popmovie1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by uttam on 8/5/16.
 */
public class ImageAdapter extends BaseAdapter {
    public Context mContext;



    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return 20;
    }

    public Object getItem(int position) {

        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {


        return MainActivity.imageViews[position];
    }


}
