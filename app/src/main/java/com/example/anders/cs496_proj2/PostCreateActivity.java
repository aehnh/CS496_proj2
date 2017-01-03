package com.example.anders.cs496_proj2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PostCreateActivity extends AppCompatActivity {
    static final int FINISH_BY_CANCEL = 100;
    static final int FINISH_BY_SUBMIT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postcreate);

        Button cancel = (Button) findViewById(R.id.post_cancel);
        Button submit = (Button) findViewById(R.id.post_submit);

        cancel.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(FINISH_BY_CANCEL);
                finish();
            }
        });

        final EditText title = (EditText) findViewById(R.id.submit_title);
        final EditText question = (EditText) findViewById(R.id.submit_question);

        submit.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().length() > 50) {
                    Toast.makeText(PostCreateActivity.this, "Word limit exceeded: " + Integer.toString(title.getText().toString().length()) + " / 50 characters", Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent();
                    i.putExtra("title", title.getText().toString());
                    i.putExtra("question", question.getText().toString());
                    setResult(FINISH_BY_SUBMIT, i);
                    finish();
                }
            }
        });
    }
}
