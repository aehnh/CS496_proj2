package com.example.anders.cs496_proj2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by q on 2017-01-03.
 */

public class PostViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postview);

        TextView title = (TextView) findViewById(R.id.post_view_title);
        TextView question = (TextView) findViewById(R.id.post_view_question);

        //TODO : set title and question by the accessed post
        String str = "";
        for (int i = 0; i < 100; i++) {
            str += "Long text\n";
        }
        title.setText("set title here");
        question.setText(str);

        Button submit = (Button) findViewById(R.id.comment_submit);
        EditText comment = (EditText) findViewById(R.id.new_comment);

        String new_comment = comment.getText().toString();

        submit.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : save a new comment and refresh the comments in the activity
            }
        });
    }

    private class CommentAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.comment, parent, false);
            }

            TextView comment_title = (TextView) findViewById(R.id.comment_title);
            TextView comment_content = (TextView) findViewById(R.id.comment_content);

            //TODO : set comment_title and comment_content
            comment_title.setText("set comment title here");
            comment_content.setText("set comment content here");

            return convertView;
        }
    }
}
