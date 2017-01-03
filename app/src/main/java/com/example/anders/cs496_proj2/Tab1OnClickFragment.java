package com.example.anders.cs496_proj2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anders.cs496_proj2.R;

/**
 * Created by q on 2016-12-26.
 */

public class Tab1OnClickFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStates) {
        View view = inflater.inflate(R.layout.tab1_onclick, container);
        TextView name = (TextView) view.findViewById(R.id.tab1_onclick_name);
        TextView number = (TextView) view.findViewById(R.id.tab1_onclick_number);
        ImageView thumbnail = (ImageView) view.findViewById(R.id.profile_image);
        final Bundle params = getArguments();

        name.setText(params.getString("name"));

        if (params.getString("from").compareTo("contact") == 0) {
            number.setText(params.getString("number"));
            thumbnail.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.cat1));
        } else {
            number.setText("From Facebook");
            thumbnail.setImageBitmap(getBitmapFromString(params.getString("thumbnail")));
        }

        LinearLayout phone_num = (LinearLayout) view.findViewById(R.id.tab1_phone_num);
        if (params.getString("from").compareTo("contact") == 0) {
            phone_num.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + params.getString("number")));
                    getActivity().startActivity(i);
                }
            });
        } else {
            phone_num.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Cannot call to the person from facebook friends list", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }

    public Bitmap getBitmapFromString(String string) {
        byte[] b = Base64.decode(string, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }
}
