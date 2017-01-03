package com.example.anders.cs496_proj2;

import android.graphics.Bitmap;

public class PackedImage {
    private Integer id;
    private Bitmap bitmap;

    public PackedImage(Integer id, Bitmap bitmap) {
        this.id = id;
        this.bitmap = bitmap;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
