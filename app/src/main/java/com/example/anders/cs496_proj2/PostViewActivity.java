package com.example.anders.cs496_proj2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by q on 2017-01-03.
 */

public class PostViewActivity extends AppCompatActivity {
    Post post;
    ListView listView;
    CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postview);

        Intent i = getIntent();
        post = Tab3Fragment.posts.get(i.getExtras().getInt("position"));

        TextView title = (TextView) findViewById(R.id.post_view_title);
        TextView question = (TextView) findViewById(R.id.post_view_question);

        title.setText(post.getTitle());
        question.setText(post.getQuestion());

        Button submit = (Button) findViewById(R.id.comment_submit);
        final EditText comment = (EditText) findViewById(R.id.new_comment);

        submit.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_comment = comment.getText().toString();
                JSONObject jobject = new JSONObject();
                try {
                    jobject.put("comment_title", "");
                    jobject.put("comment_content", new_comment);

                    new SaveNewComment().execute(jobject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        listView = (ListView) findViewById(R.id.comments);
        adapter = new CommentAdapter();
        listView.setAdapter(adapter);
    }

    private class CommentAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return post.getComments().length();
        }

        @Override
        public Object getItem(int position) {
            JSONObject jobject = null;
            int len = getCount();
            try {
                jobject = post.getComments().getJSONObject(len - 1 - position);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jobject;
        }

        @Override
        public long getItemId(int position) {
            return post.getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.comment, parent, false);
                System.out.println("convert view : " + (convertView == null));
            }

            TextView comment_title = (TextView) convertView.findViewById(R.id.comment_title);
            TextView comment_content = (TextView) convertView.findViewById(R.id.comment_content);

            //comment_title.setText(((Comment)getItem(position)).getCommentTitle());
            comment_title.setText("Anonymous");
            String comment_content_str = "Something's wrong...";
            try {
                comment_content_str = ((JSONObject)getItem(position)).getString("comment_content");
            } catch (Exception e) {
                e.printStackTrace();
            }
            comment_content.setText(comment_content_str);

            return convertView;
        }
    }

    public class SaveNewComment extends AsyncTask<JSONObject, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            JSONObject new_comment = null;
            try {
                URL url = new URL("http://ec2-52-79-95-160.ap-northeast-2.compute.amazonaws.com:3000/post_comment");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                JSONArray comments = post.getComments();
                comments.put(params[0]);

                new_comment = new JSONObject();
                new_comment.put("comments", comments);
                new_comment.put("id", Integer.toString(post.getId()));

                OutputStream out_stream = conn.getOutputStream();

                System.out.println("new post : " + new_comment.toString());

                out_stream.write(new_comment.toString().getBytes("UTF-8"));
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

            return new_comment;
        }

        @Override
        protected void onProgressUpdate(Void... params) {

        }

        @Override
        protected void onPostExecute(JSONObject param) {
            try {
                post.setComments(param.getJSONArray("comments"));
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
