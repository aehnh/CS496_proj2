package com.example.anders.cs496_proj2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Post> {

    public CustomAdapter(Context context, int resource, ArrayList<Post> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
/*
        if(v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(android.R.layout.simple_list_item_2, null);
        }
*/
        Post post = getItem(position);

        TextView textView1 = (TextView)convertView.findViewById(android.R.id.text1);
        TextView textView2 = (TextView)convertView.findViewById(android.R.id.text2);
        textView1.setText(post.getTitle());
        textView2.setText(post.getQuestion());

        return v;
    }
}
