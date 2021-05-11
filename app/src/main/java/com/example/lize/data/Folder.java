package com.example.lize.data;

import java.util.ArrayList;

public class Folder {

    private String name;
    private final ArrayList<Note> notes;

    public Folder(String name) {
        this.name = name;
        this.notes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void addNote(Note note){
        note.setFolderTAG(this.name);
        notes.add(note);
    }
}

