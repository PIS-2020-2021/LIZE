package com.example.lize.data;

import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

// Note Model Class
public class Note {

    private String title;
    private String text_plain;
    private String text_html;
    private String selfID;
    private String ambitoID;
    private String folderTAG;

    private ArrayList<ImageView> images;
    private ArrayList<File> files;

    public Note(String title, String text_plain, String text_html) {
        this.title = title;
        this.text_plain = text_plain;
        this.text_html = text_html;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) { this.title = title; }

    public String getText_plain() {
        return text_plain;
    }

    public void setText_plain(String text_plain) { this.text_plain = text_plain; }

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

    public String getText_html() {
        return text_html;
    }

    public void setText_html(String text_html) {
        this.text_html = text_html;
    }
}
