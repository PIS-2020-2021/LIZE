package com.example.lize.data;

import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

// Note Model Class
public class Note {

    private String title;
    private String text;
    private String selfID;
    private String ambitoID;
    private String folderTAG;

    private ArrayList<ImageView> images;
    private ArrayList<File> files;

    public Note(){};

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

    public String getAmbitoID() {
        return ambitoID;
    }

    public void setAmbitoID(String ambitoID) {
        this.ambitoID = ambitoID;
    }

    public String getFolderTAG() {
        return folderTAG;
    }

    public void setFolderTAG(String folderTAG) {
        this.folderTAG = folderTAG;
    }

    public ArrayList<ImageView> getImages() {
        return images;
    }

    public void setImages(ArrayList<ImageView> images) {
        this.images = images;
    }

    public String getSelfID() { return selfID; }

    public void setSelfID(String selfID) { this.selfID = selfID; }

}
