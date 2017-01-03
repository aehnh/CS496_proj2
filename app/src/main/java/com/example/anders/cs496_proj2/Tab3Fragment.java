package com.example.anders.cs496_proj2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by q on 2016-12-30.
 */

public class Tab3Fragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab3, container, false);

        final List<Post> posts = new ArrayList<Post>();
        // TODO json from server
        String json = "";
        try {
            JSONObject jsonResponse = new JSONObject(json);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("posts");

            for(int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String question = jsonChildNode.optString("question");
                String description = jsonChildNode.optString("description");
                JSONArray jsonComments = jsonChildNode.getJSONArray("comments");
                ArrayList<String> comments = new ArrayList<String>();
                for(int j = 0; j < jsonComments.length(); j++) {
                    JSONObject jsonComment = jsonComments.getJSONObject(j);
                    comments.add(jsonComment.optString("comment"));
                }

                Post post = new Post(question, description, comments);

                posts.add(post);
            }
        } catch(JSONException e) {
            Toast.makeText(getActivity().getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        ListView listView = (ListView)view.findViewById(R.id.listView1);
        CustomAdapter adapter = new CustomAdapter(getActivity(), android.R.layout.simple_list_item_2, posts);
        listView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }
}
