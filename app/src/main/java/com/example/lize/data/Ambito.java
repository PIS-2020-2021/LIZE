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

    private Map<String, Folder> folders;

    public Ambito(){}

    public Ambito(String name, int color) {
        this.name = name;
        this.color = color;
        this.folders = new HashMap<>();
        folders.put(Folder.BASE_FOLDER_NAME, new Folder(Folder.BASE_FOLDER_NAME));
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

    public Folder getFolder(String folderName) { return folders.get(folderName); }

    public ArrayList<Folder> getFolders() { return new ArrayList(folders.values()); }

    public void putFolder(Folder folder){
        folder.setAmbitoID(selfID);
        if (folders.get(folder.getName()) == null)
            folders.put(folder.getName(), folder);
    }

    public String getSelfID() {
        return selfID;
    }

    public void setSelfID(String selfID) {
        this.selfID = selfID;
        for (Folder folder : this.folders.values()){
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
        return folders.get(Folder.BASE_FOLDER_NAME).getNotes();
    }

    /**
     * Pone la note en el ámbito según la carpeta a la que pertenece. Si esta carpeta no figura en
     * el mapa, la añade. Posteriormente, añade la nota tanto en la carpeta {@link Folder#BASE_FOLDER_NAME}
     * como en la carpeta a la que pertenece {@link Folder#addNote(Note)}.
     * @param note nota a añadir al ámbito.
     */
    public void putNote(Note note){
        String folderName = note.getFolderTAG();
        if (folderName == null) return;
        if(!folders.containsKey(folderName)) folders.put(folderName, new Folder(folderName, selfID));
        if (!Folder.BASE_FOLDER_NAME.equals(folderName)) folders.get(Folder.BASE_FOLDER_NAME).addNote(note);
        folders.get(folderName).addNote(note);
    }

}
