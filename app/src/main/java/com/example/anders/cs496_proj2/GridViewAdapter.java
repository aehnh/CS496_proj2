package com.example.anders.cs496_proj2;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {
    private Context context;

    final public static ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();

    public Integer[] imageIds = {
            R.drawable.dog1, R.drawable.dog2, R.drawable.dog3, R.drawable.dog4,
            R.drawable.cat1, R.drawable.cat2, R.drawable.cat3, R.drawable.cat4,
            R.drawable.lion1, R.drawable.lion2, R.drawable.lion3,
            R.drawable.otter1, R.drawable.otter2, R.drawable.otter3,
            R.drawable.polarbear1, R.drawable.polarbear2, R.drawable.polarbear3
    };

    public GridViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @Override
    public Object getItem(int position) {
        return bitmaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image;
        if (convertView == null) {
            image = new ImageView(context);
            final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 113, this.context.getResources().getDisplayMetrics());
            final int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 113, this.context.getResources().getDisplayMetrics());
            image.setLayoutParams(new GridView.LayoutParams(width, height));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            image = (ImageView) convertView;
        }
        image.setImageBitmap(bitmaps.get(position));
        return image;
    }
}
