package com.example.lize.data;


public class Audio {

    private final String ID;
    private final String address;
    private final long duration;

    /**
     * Constructor de la clase
     * @param id ID del Audio
     * @param localPath PATH local del Audio
     * @param duration Duracion del Audio
     */
    public Audio(String id,String localPath, long duration) {
        this.ID = id;
        this.address = localPath;
        this.duration = duration;
    }

    /**
     * Metodo para conseguir el PATH de un Audio
     * @return PATH del Audio
     */
    public String getAddress () {
        return this.address;
    }

    /**
     * Metodo para conseguir la duracion de un Audio
     * @return Duracion del Audio
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Metodo para conseguir el ID de un Audio
     * @return ID del Audio
     */
    public String getID() {
        return ID;
    }

}
