package com.example.lize.data;

import java.util.ArrayList;

public class User {
    private String first;
    private String last;
    private String mail;
    private String password;
    private String selfID;

    // TODO: username implementation! private String username;

    private ArrayList<Ambito> ambitos;

    public User(){};

    public User(String mail, String password, String first, String last){
        this.mail = mail;
        this.password = password;
        this.first = first;
        this.last = last;
        ambitos = new ArrayList<>();
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

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

    public void setSelfID(String selfID) { this.selfID = selfID; }

    public ArrayList<Ambito> getAmbitos() {
        return ambitos;
    }

    public boolean addAmbito(Ambito ambito){
        return ambitos.add(ambito);
    }

    public void setAmbitos(ArrayList<Ambito> ambitos) { this.ambitos = ambitos; }
}
