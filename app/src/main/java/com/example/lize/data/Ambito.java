package com.example.lize.data;

import java.util.ArrayList;

public class Ambito {
    public static final String BASE_AMBITO_NAME = "Personal";
    public static final int BASE_AMBITO_COLOR = 1;

    private String name;
    private int color;
    private String selfID;
    private String userID;

    private ArrayList<Folder> folders;

    public Ambito(){}

    public Ambito(String name, int color) {
        this.name = name;
        this.color = color;
        this.folders = new ArrayList<>();
        folders.add(new Folder(Folder.BASE_FOLDER_NAME));
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
        folder.setAmbitoID(selfID);
        return folders.add(folder);
    }

    public String getSelfID() {
        return selfID;
    }

    public void setSelfID(String selfID) {
        this.selfID = selfID;
        for (Folder folder : this.folders){
            folder.setAmbitoID(selfID);
        }
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<Note> getNotes() {
        return folders.get(0).getNotes();
    }
}
