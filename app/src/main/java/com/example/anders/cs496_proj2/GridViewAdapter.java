package com.example.anders.cs496_proj2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class GridViewAdapter extends BaseAdapter {
    private Context context;

    final public static ArrayList<PackedImage> bitmaps = new ArrayList<PackedImage>();
    public static int nextId = 0;

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
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int dimen = (metrics.widthPixels - 8) / 3;
        ImageView image;
        if (convertView == null) {
            image = new ImageView(context);
            //final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 113, this.context.getResources().getDisplayMetrics());
            //final int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 113, this.context.getResources().getDisplayMetrics());
            image.setLayoutParams(new GridView.LayoutParams(dimen, dimen));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            image = (ImageView) convertView;
        }
        image.setImageBitmap(bitmaps.get(position).getBitmap());
        return image;
    }
}
