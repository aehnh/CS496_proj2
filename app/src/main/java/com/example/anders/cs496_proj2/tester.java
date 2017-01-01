package com.example.anders.cs496_proj2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class tester extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_tab);

        TextView textView = (TextView)findViewById(R.id.textView1234);
        textView.setText(Tab2Fragment.gotten.get(0));
    }
}
