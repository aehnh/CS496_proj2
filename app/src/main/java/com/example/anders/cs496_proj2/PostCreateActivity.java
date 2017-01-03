package com.example.anders.cs496_proj2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class PostCreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postcreate);

        final EditText editText1 = (EditText)findViewById(R.id.editText2);
        final EditText editText2 = (EditText)findViewById(R.id.editText3);
        Button button1 = (Button)findViewById(R.id.button2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = editText1.getText().toString();
                String description = editText2.getText().toString();

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("question", question);
                    jsonObject.put("description", description);
                    jsonObject.put("comments", new JSONObject());
                    // TODO send jsonObject to server
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
