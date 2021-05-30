package com.example.lize.data;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.io.File;

public class Image extends File {
    private String id;
    private Bitmap bitmap;

    public Image(@NonNull String pathname) {
        super(pathname);
    }

    public Image(File cacheDir, String valueOf) {
        super(cacheDir,valueOf);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
