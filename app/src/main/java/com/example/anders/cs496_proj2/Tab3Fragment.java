package com.example.anders.cs496_proj2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

public class Tab3Fragment extends Fragment {
    static final int NEW_POST_REQUEST = 99;
    static int id;

    static ArrayList<Post> posts;
    private View view;
    private ListView listView;
    private CustomAdapter adapter;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab3, container, false);
        id = 0;
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
                i.putExtra("position", position);
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
            posts = new ArrayList<>();
            try {
                JSONArray jarray = new JSONArray(param);
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jobject = jarray.getJSONObject(i);
                    Post post = new Post(Integer.parseInt(jobject.getString("id")), jobject.getString("title"), jobject.getString("question"), jobject.getJSONArray("comments"));
                    posts.add(post);
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
                new_post.put("id", Integer.toString(id));
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
                String id_string = param.getString("id");
                String title = param.getString("title");
                String question = param.getString("question");
                JSONArray comments = param.getJSONArray("comments");

                Post new_post = new Post(Integer.parseInt(id_string), title, question, comments);
                posts.add(new_post);
                id++;
            } catch (Exception e) {

            }
            adapter.notifyDataSetChanged();
        }
    }

    public Post getPost(int position) { return posts.get(position); }
}
