package com.example.anders.cs496_proj2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by q on 2016-12-30.
 */

public class Tab3Fragment extends Fragment  {
    static final int NEW_POST_REQUEST = 99;

    ArrayList<Post> posts;
    View view;
    ListView listView;
    CustomAdapter adapter;
    FloatingActionButton fab;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.tab3, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new LoadPosts().execute();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        /*
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
        */
        listView = (ListView)view.findViewById(R.id.listView1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity().getApplicationContext(), PostViewActivity.class);
                i.putExtra("post", posts.get(position));
                startActivity(i);
            }
        });

        fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), PostCreateActivity.class);
                startActivityForResult(i, NEW_POST_REQUEST);
            }
        });

        new LoadPosts().execute();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("wtf");
        switch (resultCode) {
            case PostCreateActivity.FINISH_BY_CANCEL :
                break;
            case PostCreateActivity.FINISH_BY_SUBMIT:
                new SaveNewPost().execute(data.getExtras().getString("title"), data.getExtras().getString("question"));
                break;
        }
    }

    public class LoadPosts extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            StringBuffer response = null;
            try {
                URL url = new URL("http://ec2-52-79-95-160.ap-northeast-2.compute.amazonaws.com:3000/posts");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                response = new StringBuffer();
                String input_line;

                while ((input_line = in.readLine()) != null) {
                    System.out.println("input_line : " + input_line);
                    response.append(input_line);
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onProgressUpdate(Void... params) {

        }

        @Override
        protected void onPostExecute(String param) {
            swipeRefreshLayout.setRefreshing(false);
            posts = new ArrayList<>();
            try {
                JSONArray jarray = new JSONArray(param);
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jobject = jarray.getJSONObject(i);
                    JSONArray comments_jarray = jobject.getJSONArray("comments");
                    ArrayList<Comment> comments = new ArrayList<>();
                    Post post;
                    if (comments_jarray.length() != 0) {
                        for (int j = 0; j < comments_jarray.length(); j++) {
                            Comment comment = new Comment(comments_jarray.getJSONObject(j).getString("comment_title"),
                                    comments_jarray.getJSONObject(j).getString("comment_content"));
                            comments.add(comment);
                        }

                        post = new Post(jobject.getString("title"), jobject.getString("question"), comments);
                        posts.add(post);
                    } else {
                        post = new Post(jobject.getString("title"), jobject.getString("question"));
                        posts.add(post);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            adapter = new CustomAdapter(getActivity(), android.R.layout.simple_list_item_2, posts);
            listView.setAdapter(adapter);
        }
    }

    public class SaveNewPost extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject new_post = null;
            try {
                URL url = new URL("http://ec2-52-79-95-160.ap-northeast-2.compute.amazonaws.com:3000/postpost");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                new_post = new JSONObject();
                new_post.put("title", params[0]);
                new_post.put("question", params[1]);
                new_post.put("image", "");
                JSONArray new_comments = new JSONArray();
                new_post.put("comments", new_comments);

                OutputStream out_stream = conn.getOutputStream();

                System.out.println("new post : " + new_post.toString());

                out_stream.write(new_post.toString().getBytes("UTF-8"));
                out_stream.close();

                conn.connect();
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return new_post;
        }

        @Override
        protected void onProgressUpdate(Void... params) {

        }

        @Override
        protected void onPostExecute(JSONObject param) {
            try {
                String title = param.getString("title");
                String question = param.getString("question");
                JSONArray comments = param.getJSONArray("comments");

                ArrayList<Comment> cmts = new ArrayList<>();
                for (int i = 0; i < comments.length(); i++) {
                    Comment comment = new Comment(comments.getJSONObject(i).getString("comment_title"),
                            comments.getJSONObject(i).getString("comment_content"));
                    cmts.add(comment);
                }

                Post new_post = new Post(title, question, cmts);
                posts.add(new_post);
            } catch (Exception e) {

            }
            adapter.notifyDataSetChanged();
        }
    }
}
