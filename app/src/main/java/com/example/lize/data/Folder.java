package com.example.lize.data;

import java.util.ArrayList;

public class Folder {
    public static final String BASE_FOLDER_NAME = "General";

    private String name;
    private String ambitoID;
    private final ArrayList<Note> notes;


    public Folder(String name) {
        this.name = name;
        this.notes = new ArrayList<>();
    }

    public Folder(String name, String ambitoID) {
        this.name = name;
        this.ambitoID = ambitoID;
        this.notes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public String getAmbitoID(){ return ambitoID; }

    public void setAmbitoID(String ambitoID){
        this.ambitoID = ambitoID;
        for (Note note : this.notes){
            note.setAmbitoID(ambitoID);
        }
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public boolean addNote(Note note){
        note.setFolderTAG(this.name);
        note.setAmbitoID(this.ambitoID);
        return notes.add(note);
    }
}

