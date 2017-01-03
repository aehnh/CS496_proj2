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
    private ArrayList<Post> posts;
    Context context;

    public CustomAdapter(Context context, int resource, ArrayList<Post> posts) {
        super(context, resource, posts);
        this.context = context;
        this.posts = posts;
    }

    @Override
    public Post getItem(int position) {
        int len = posts.size();
        return posts.get(len - 1 - position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        Post post = getItem(position);

        TextView textView1 = (TextView)convertView.findViewById(android.R.id.text1);
        TextView textView2 = (TextView)convertView.findViewById(android.R.id.text2);
        textView1.setText(post.getTitle());
        if(post.getQuestion().length() > 100) {
            textView2.setText(post.getQuestion().substring(0, 99) + " ...");
        } else {
            textView2.setText(post.getQuestion());
        }

        return convertView;
    }
}
