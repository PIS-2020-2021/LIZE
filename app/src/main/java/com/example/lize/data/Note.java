package com.example.lize.data;

import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

// Note Model Class
public class Note {

    private String title;
    private String text;

    private ArrayList<ImageView> images;
    private ArrayList<File> files;
    public Note(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public ArrayList<ImageView> getImages() {
        return images;
    }

    public void setImages(ArrayList<ImageView> images) {
        this.images = images;
    }

    // TODO: Note ID implementation
    public String getID() {
        return getText();
    }
}
