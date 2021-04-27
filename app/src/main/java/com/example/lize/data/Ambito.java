package com.example.lize.data;

import java.util.ArrayList;

public class Ambito {
    public static final String BASE_AMBITO_NAME = "Personal";

    private String name;
    private int color;
    private String selfID;
    private String userID;

    private ArrayList<Folder> folders;

    public Ambito(){}

    public Ambito(String name, int color, String userID) {
        this.name = name;
        this.color = color;
        this.userID = userID;
        this.folders = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ArrayList<Folder> getFolders() {
        return folders;
    }

    public void setFolders(ArrayList folders) {
        this.folders = folders;
    }

    public boolean addFolder(Folder folder){
        return folders.add(folder);
    }

    public String getSelfID() {
        return selfID;
    }

    public void setSelfID(String selfID) { this.selfID = selfID; }

    public String getUserID() {
        return userID;
    }

}
