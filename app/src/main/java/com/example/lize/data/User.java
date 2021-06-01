package com.example.lize.data;

import java.util.ArrayList;
import java.util.Collections;

public class User {
    private String first;
    private String last;
    private String mail;
    private String password;
    private String selfID;
    private ArrayList<Ambito> ambitos;

    /**
     * Constructor de la clase
     * @param mail Email del User (username)
     * @param password Contraseña del User
     * @param first Nombre del User
     * @param last Apellidos del User
     */
    public User(String mail, String password, String first, String last) {
        this.mail = mail;
        this.password = password;
        this.first = first;
        this.last = last;
        this.ambitos = new ArrayList<>();
        addAmbito(new Ambito(Ambito.BASE_AMBITO_NAME, Ambito.BASE_AMBITO_COLOR));
    }

    /**
     * Metodo para conseguir el email del User
     * @return Email del User
     */
    public String getMail() {
        return mail;
    }

    /**
     * Metodo para establecer el email del User
     * @param mail Email del User
     */
    public void setMail(String mail) { this.mail = mail; }

    /**
     * Metodo para conseguir la contraseña del User
     * @return Contraseña del User
     */
    public String getPassword() {
        return password;
    }

    /**
     * Metodo para establecer la contraseña del User
     * @param password Contraseña del User
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Metodo para conseguir el nombre del User
     * @return Nombre del User
     */
    public String getFirst() {
        return first;
    }

    /**
     * Metodo para establecer el nombre del User
     * @param first Nombre del User
     */
    public void setFirst(String first) {
        this.first = first;
    }

    /**
     * Metodo para conseguir los apellidos del User
     * @return Apellidos del User
     */
    public String getLast() {
        return last;
    }

    /**
     * Metodo para establecer los apellidos del User
     * @param last Apellidos del User
     */
    public void setLast(String last) {
        this.last = last;
    }

    /**
     * Metodo para conseguir el ID del User
     * @return ID del User
     */
    public String getSelfID() {
        return selfID;
    }

    /**
     * Metodo para establecer el ID del User
     * @param selfID ID del User
     */
    public void setSelfID(String selfID) {
        this.selfID = selfID;
        for (Ambito ambito: this.ambitos) ambito.setUserID(selfID);
    }

    /**
     * Metodo para conseguir los Ambitos del User
     * @return Ambitos del User
     */
    public ArrayList<Ambito> getAmbitos() {
        return ambitos;
    }

    /**
     * Metodo para establecer los Ambitos de un User
     * @param ambitos Ambitos del User
     */
    public void setAmbitos(ArrayList<Ambito> ambitos) { this.ambitos = ambitos; }

    /**
     * Metodo para añadir un Ambito a la lista de Ambitos del User
     * @param ambito Ambito a añadir
     */
    public void addAmbito(Ambito ambito) {
        ambito.setUserID(selfID);
        ambito.setPosition(ambitos.size());
        ambitos.add(ambito);
    }

    /**
     * Metodo para conseguir el numero total de Notas creadas por un User
     * @return Numero total de Notas del User
     */
    public String getTotalNotes() {
        int totalNotes = 0;
        for (Ambito ambito: this.ambitos) totalNotes += ambito.getNumberOfNotes();
        return String.valueOf(totalNotes);
    }

    /**
     * Metodo para conseguir los colores de Ambito ya escogidos por el User
     * @return Colores de Ambito ya escogidos
     */
    public ArrayList<Integer> getColorsTaken(){
        ArrayList<Integer> ambitoColors = new ArrayList<>();
        for (Ambito ambito: this.ambitos) ambitoColors.add(ambito.getColor());
        Collections.sort(ambitoColors);
        return ambitoColors;
    }

    /**
     * Metodo paar conseguir toda la informacion del User
     * @return Informacion del User
     */
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

    /**
     * Metodo para cambiar la posicion de un Ambito en la lista de Ambitos del User
     * @param initialPosition Posicion inicial del Ambito
     * @param finalPosition Posicion final del Ambito
     */
    public void swapAmbitos(int initialPosition, int finalPosition) {
        ambitos.get(initialPosition).setPosition(finalPosition);
        ambitos.get(finalPosition).setPosition(initialPosition);
        Collections.swap(ambitos, initialPosition, finalPosition);
    }

}
