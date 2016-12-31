package com.example.anders.cs496_proj2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ImageSliderActivity extends AppCompatActivity {
    int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageslider);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent i = getIntent();
        position = i.getExtras().getInt("id");

        GridViewAdapter gridViewAdapter = new GridViewAdapter(this);
        List<ImageView> images = new ArrayList();

        for (int j = 0; j < gridViewAdapter.getCount(); j++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(gridViewAdapter.imageIds[j]);
            images.add(imageView);
        }

        ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(images);
        ViewPager viewPager = (ViewPager) findViewById(R.id.imageSlider);
        viewPager.setAdapter(imageSliderAdapter);
        viewPager.setCurrentItem(position);
    }
}
