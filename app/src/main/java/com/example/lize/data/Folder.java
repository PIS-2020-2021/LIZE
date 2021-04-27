package com.example.lize.data;

import java.util.ArrayList;

public class Folder {
    public static final String BASE_FOLDER_NAME = "General";

    private String name;
    private String ambitoID;
    private ArrayList<Note> notes;

    public Folder(){};

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

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Note> notes) { this.notes = notes; }

    public boolean addNote(Note note){
        return notes.add(note);
    }
}

