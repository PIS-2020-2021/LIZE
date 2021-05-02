package com.example.lize.data;

import androidx.annotation.NonNull;

import java.io.File;

public class Documento extends File {

    String name;
    String path;

    public Documento(String name,String path){
        super(path);
        this.name = name;
    }

    public Documento(String path){
        super(path);
        this.path = path;
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
