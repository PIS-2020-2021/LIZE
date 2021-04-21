package com.example.lize.data;

import java.util.ArrayList;

public class Ambito {
    private String ambitoName;
    private ArrayList<Folder> ambitoFolders;

    public Ambito(String name) {
        this.ambitoName = name;
        this.ambitoFolders = new ArrayList<>();
    }

    public String getAmbitoName() {
        return ambitoName;
    }

    public ArrayList<Folder> getAmbitoFolders() {
        return ambitoFolders;
    }

    public boolean addFolder(Folder folder){
        return ambitoFolders.add(folder);
    }
}
