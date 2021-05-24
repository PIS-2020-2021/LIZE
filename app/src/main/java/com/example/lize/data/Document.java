package com.example.lize.data;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.File;

public class Document {//extends File{

    private String name;
    private Uri Url;
    private String id;
    private String path;
    private byte[] bytes;


    public Document(Uri uri){
        this.Url = uri;
    }
    public Document(String name, Uri Url){

        if (name.trim().equals("")) {
            this.name = "No Name";
        }

        this.name = name;
        this.Url = Url;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUrl() {
        return Url;
    }

    public void setUrl(Uri url) {
        Url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
