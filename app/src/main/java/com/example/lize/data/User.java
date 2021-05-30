package com.example.lize.data;

import java.util.ArrayList;
import java.util.Collections;

public class User {
    private String first;
    private String last;
    private String mail;
    private String password;
    private String selfID;

    // TODO: username implementation! private String username;

    private ArrayList<Ambito> ambitos;

    public User(String mail, String password, String first, String last){
        this.mail = mail;
        this.password = password;
        this.first = first;
        this.last = last;
        this.ambitos = new ArrayList<>();
        addAmbito(new Ambito(Ambito.BASE_AMBITO_NAME, Ambito.BASE_AMBITO_COLOR));
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) { this.mail = mail; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getSelfID() {
        return selfID;
    }

    public void setSelfID(String selfID) {
        this.selfID = selfID;
        for (Ambito ambito: this.ambitos)
            ambito.setUserID(selfID);
    }

    public ArrayList<Ambito> getAmbitos() {
        return ambitos;
    }

    public void addAmbito(Ambito ambito){
        ambito.setUserID(selfID);
        ambito.setPosition(ambitos.size());
        ambitos.add(ambito);
    }

    public void setAmbitos(ArrayList<Ambito> ambitos) { this.ambitos = ambitos; }

    public String getTotalNotes() {
        int totalNotes = 0;
        for (Ambito ambito: this.ambitos) {
            totalNotes += ambito.getNumberOfNotes();
        }
        return String.valueOf(totalNotes);
    }

    public ArrayList<Integer> getColorsTaken(){
        ArrayList<Integer> ambitoColors = new ArrayList<>();
        for (Ambito ambito: this.ambitos)
            ambitoColors.add(ambito.getColor());
        Collections.sort(ambitoColors);
        return ambitoColors;
    }

    public ArrayList<String> getInfoUser(){
        ArrayList<String> info = new ArrayList<>();
        info.add(getFirst());
        info.add(getLast());
        info.add(getMail());
        info.add(getPassword());
        info.add(String.valueOf(ambitos.size()));
        info.add(getSelfID());
        info.add(getTotalNotes());
        return info;
    }

    public void swapAmbitos(int initialPosition, int finalPosition){
        ambitos.get(initialPosition).setPosition(finalPosition);
        ambitos.get(finalPosition).setPosition(initialPosition);
        Collections.swap(ambitos, initialPosition, finalPosition);
    }

}
