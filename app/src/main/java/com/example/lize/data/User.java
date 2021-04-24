package com.example.lize.data;

import java.util.ArrayList;

public class User {
    private String mail;
    private String password;
    private String first;
    private String last;
    private ArrayList<Ambito> userAmbitos;

    public User(String mail, String password, String first, String last){
        this.mail = mail;
        this.password = password;
        this.first = first;
        this.last = last;
        userAmbitos = new ArrayList<>();
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

    public ArrayList<Ambito> getUserAmbitos() {
        return userAmbitos;
    }

    public boolean addAmbito(Ambito ambito){
        return userAmbitos.add(ambito);
    }


}
