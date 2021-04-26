package com.example.lize.data;

import java.util.ArrayList;

public class Ambito {

    private String ambitoName;
    private int ambitoColor;
    private ArrayList<Folder> ambitoFolders;

    public Ambito(){}

    public Ambito(String name, int color) {
        this.ambitoName = name;
        this.ambitoColor = color;
        this.ambitoFolders = new ArrayList<>();
    }

    public String getAmbitoName() {
        return ambitoName;
    }

    public void setAmbitoName(String ambitoName) {
        this.ambitoName = ambitoName;
    }

    public int getAmbitoColor() {
        return ambitoColor;
    }

    public void setAmbitoColor(int ambitoColor) {
        this.ambitoColor = ambitoColor;
    }

    public ArrayList<Folder> getAmbitoFolders() {
        return ambitoFolders;
    }

    public void setAmbitoFolders(ArrayList arrayList) {
        ambitoFolders = arrayList;
    }

    public boolean addFolder(Folder folder){
        return ambitoFolders.add(folder);
    }

}
