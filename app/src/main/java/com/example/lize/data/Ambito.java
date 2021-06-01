package com.example.lize.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Ambito {
    public static final String BASE_AMBITO_NAME = "Personal";
    public static final int BASE_AMBITO_COLOR = 1;

    private String name;
    private int color;
    private String selfID;
    private String userID;
    private int position;
    private final ArrayList<Note> notes;
    private final Map<String, Folder> folders;

    /**
     * Constructor de la clase
     * @param name Nombre del ambito
     * @param color Color del ambito
     */
    public Ambito(String name, int color) {
        this.name = name;
        this.color = color;
        this.notes = new ArrayList<>();
        this.folders = new HashMap<>();
    }

    /**
     * Metodo para conseguir el nombre del ambito
     * @return Nombre del ambito
     */
    public String getName() {
        return name;
    }

    /**
     * Metodo para establecer el nombre de un Ambito
     * @param name Nombre del Ambito
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Metodo para conseguir el color del ambito
     * @return Color del ambito
     */
    public int getColor() {
        return color;
    }

    /**
     * Metodo para establecer el color de un Ambito
     * @param color Color del Ambito
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Metodo para conseguir el ID del ambito
     * @return ID del ambito
     */
    public String getSelfID() {
        return selfID;
    }

    /**
     * Metodo para establecer el ID de un Ambito
     * @param selfID ID del Ambito
     */
    public void setSelfID(String selfID) {
        this.selfID = selfID;
        for (Note note : this.notes) note.setAmbitoID(selfID);
    }

    /**
     * Metodo para conseguir el ID del User
     * @return ID del User
     */
    public String getUserID() { return userID; }

    /**
     * Metodo para establecer el ID de un User
     * @param userID ID del User
     */
    public void setUserID(String userID) { this.userID = userID; }

    /**
     * Metodo para conseguir la posición del ambito
     * @return posicion del ambito
     */
    public int getPosition() { return position; }

    /**
     * Metodo para establecer la posicion de un Ambito
     * @param position Posicion del Ambito
     */
    public void setPosition(int position) { this.position = position; }

    /**
     * Metodo para conseguir las carpetas del Ambito
     * @return Folders del Ambito
     */
    public ArrayList<Folder> getFolders() { return new ArrayList<>(folders.values()); }

    /**
     * Metodo para conseguir una de las Carpetas del Ambito
     * @param folderName Nombre de la Carpeta
     * @return Carpeta del Ambito
     */
    public Folder getFolder(String folderName) { return folders.get(folderName); }

    /**
     * Metodo para añadir una Carpeta a un Ambito
     * @param folderName Nombre de la Carpeta
     */
    public void addFolder(String folderName) {
        if (!folders.containsKey(folderName)) folders.put(folderName, new Folder(folderName));
    }

    /**
     * Metodo para conseguir las Notas de un Ambito
     * @return Notas de un Ámbito
     */
    public ArrayList<Note> getNotes() { return this.notes; }

    /**
     * Metodo para conseguir el numero de Notas que tiene un Ambito
     * @return Numero de Notas de un Ambito
     */
    public int getNumberOfNotes() { return this.notes.size(); }

    /**
     * Metodo para añadir una Nota a un Ambito
     * @param note Nota a añadir
     */
    public void addNote(Note note) {
        this.notes.add(note);
        note.setAmbitoID(selfID);

        String folderName = note.getFolderTAG();
        if (folderName != null) {
            addFolder(folderName);
            getFolder(folderName).addNote(note);
        }
    }

    /**
     * Metodo para eliminar una Nota de un Ambito
     * @param note Nota a eliminar
     */
    public void removeNote(Note note) {
        this.notes.remove(note);
        String folderName = note.getFolderTAG();
        if (folderName != null && folders.containsKey(folderName)) folders.get(folderName).getNotes().remove(note);
        }

    /**
     * Metodo para eliminar una Carpeta de un Ambito
     * @param folderName NOmbre de la Carpeta a eliminar
     */
    public void removeFolder(String folderName) {
        Folder removed = this.folders.remove(folderName);
        if (removed != null) for (Note note : removed.getNotes()) this.notes.remove(note);
    }
}
