package com.example.lize.data;

import java.util.ArrayList;

public class Folder {
    private String name;
    private final ArrayList<Note> notes;

    /**
     * Constructor de la clase
     * @param name Nombre de la Carpeta
     */
    public Folder(String name) {
        this.name = name;
        this.notes = new ArrayList<>();
    }

    /**
     * Metodo para conseguir el nombre de la Carpeta
     * @return Nombre de la Carpeta
     */
    public String getName() {
        return name;
    }

    /**
     * Metodo para establecer el nombre de la Carpeta
     * @param name Nombre de la Carpeta
     */
    public void setName(String name) { this.name = name; }

    /**
     * Metodo para ocnseguir las Notas de una Carpeta
     * @return Notas de la Carpeta
     */
    public ArrayList<Note> getNotes() {
        return notes;
    }

    /**
     * Metodo para añadir una Nota a una Carpeta
     * @param note Nota a añadir
     */
    public void addNote(Note note) {
        note.setFolderTAG(this.name);
        notes.add(note);
    }
}

