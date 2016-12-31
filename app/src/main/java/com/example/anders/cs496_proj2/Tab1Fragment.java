package com.example.anders.cs496_proj2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by q on 2016-12-30.
 */

public class Tab1Fragment extends Fragment {
    View view;
    TextView text_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.base_tab, container, false);
        text_view = (TextView) view.findViewById(R.id.tab1_text);

        new Thread() {
            public void run() {
                String response = getHelloWorld();

                Bundle bun = new Bundle();
                bun.putString("hello_world", getHelloWorld());
                Message msg = handler.obtainMessage();
                msg.setData(bun);
                handler.sendMessage(msg);
            }
        }.start();

        return view;
    }

    private String getHelloWorld() {
        StringBuffer response = new StringBuffer();
        String hello_world = "";
        try {
            URL url = new URL("http://ec2-52-79-95-160.ap-northeast-2.compute.amazonaws.com:3000");
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
            System.out.println(response.toString());

            JSONArray jarray = new JSONArray(response.toString());
            hello_world = jarray.getJSONObject(0).getString("text");
            System.out.println("wtf : " + hello_world);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hello_world;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            String hello_world = bun.getString("hello_world");
            text_view.setText(hello_world);
        }
    };
}
