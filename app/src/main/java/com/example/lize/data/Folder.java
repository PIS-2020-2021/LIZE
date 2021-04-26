package com.example.lize.data;

import java.util.ArrayList;

public class Folder {
    private String folderName;
    private ArrayList<Note> mFolderNotes;

    public Folder(){};

    public Folder(String name) {
        this.folderName = name;
        this.mFolderNotes = new ArrayList<>();
    }

    public String getFolderName() {
        return folderName;
    }

    public ArrayList<Note> getFolderNotes() {
        return mFolderNotes;
    }

    public boolean addNote(Note note){
        return mFolderNotes.add(note);
    }
}

