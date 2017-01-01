package com.example.anders.cs496_proj2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by q on 2016-12-30.
 */

public class Tab3Fragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.base_tab, container, false);

        TextView textView = (TextView)view.findViewById(R.id.textView1234);
        if(!Tab2Fragment.gotten.isEmpty()) {
            //textView.setText((Tab2Fragment.gotten).get(0));
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), tester.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
