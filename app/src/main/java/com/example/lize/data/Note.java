package com.example.lize.data;

import java.util.Date;

public class Note {
    private String title;
    private String text_plain;
    private String text_html;
    private Date lastUpdate;

    private String folderTAG;

    private String selfID;
    private String ambitoID;
    private String documentsID;
    private String imagesID;
    private String audiosID;

    private Boolean haveDocuments;
    private Boolean haveImages;
    private Boolean haveAudios;


    /**
     * Constructor de la clase
     * @param title Titulo de la Nota
     * @param text_plain Texto sin efectos de la Nota
     * @param text_html Texto con efectos sen HTML de la Nota
     */
    public Note(String title, String text_plain, String text_html) {
        this.title = title;
        this.text_plain = text_plain;
        this.text_html = text_html;
        this.lastUpdate = new Date();
    }

    /**
     * Metodo para conseguir el titulo de la Nota
     * @return Titulo de la Nota
     */
    public String getTitle() { return title; }

    /**
     * Metodo para establecer el titulo de la Nota
     * @param title Titulo de la Nota
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Metodo para conseguir el texto sin efectos de la Nota
     * @return Texto sin efectos de la Nota
     */
    public String getText_plain() { return text_plain; }

    /**
     * Metodo para establecer el texto sin efectos de la Nota
     * @param text_plain Texto sin efectos de la Nota
     */
    public void setText_plain(String text_plain) { this.text_plain = text_plain; }

    /**
     * Metodo para conseguir el texto con efectos de la Nota
     * @return Texto con efectos de la Nota
     */
    public String getText_html() { return text_html; }

    /**
     * Metodo para establecer el texto con efectos de la Nota
     * @param text_html Texto con efectos de la Nota
     */
    public void setText_html(String text_html) { this.text_html = text_html; }

    /**
     * Metodo para conseguir la fecha de la ultima actualización de la Nota
     * @return Fecha de la ultima actualización de la Nota
     */
    public Date getLastUpdate() { return lastUpdate; }

    /**
     * Metodo para establecer la fecha de la ultima actualización de la Nota
     * @param lastUpdate Fecha de la ultima actualización de la Nota
     */
    public void setLastUpdate(Date lastUpdate) { this.lastUpdate = lastUpdate; }

    /**
     * Metodo para conseguir el TAG de la Carpeta de la Nota
     * @return TAG de la Carpeta de la Nota
     */
    public String getFolderTAG() { return folderTAG; }

    /**
     * Metodo para establecer el TAG de la Carpeta de la Nota
     * @param folderTAG TAG de la Carpeta de la Nota
     */
    public void setFolderTAG(String folderTAG) { this.folderTAG = folderTAG; }

    /**
     * Metodo para conseguir el ID de la Nota
     * @return ID de la Nota
     */
    public String getSelfID() { return selfID; }

    /**
     * Metodo para establecer el ID de la Nota
     * @param selfID ID de la Nota
     */
    public void setSelfID(String selfID) { this.selfID = selfID; }

    /**
     * Metodo para conseguir el ID del Ambito de la Nota
     * @return ID del Ambito de la Nota
     */
    public String getAmbitoID() { return ambitoID; }

    /**
     * Metodo para establecer el ID del Ambito de la Nota
     * @param ambitoID ID del Ambito de la Nota
     */
    public void setAmbitoID(String ambitoID) { this.ambitoID = ambitoID; }

    /**
     * Metodo para conseguir el ID de los Documentos de la Nota
     * @return ID de los Documentos de la Nota
     */
    public String getDocumentsID() { return documentsID; }

    /**
     * Metodo para establecer el ID de los Documentos de la Nota
     * @param documentsID ID de los Documentos de la Nota
     */
    public void setDocumentsID(String documentsID) { this.documentsID = documentsID; }

    /**
     * Metodo para conseguir el ID de las Imagenes de la Nota
     * @return ID de las Imagenes de la Nota
     */
    public String getImagesID() { return imagesID; }

    /**
     * Metodo para establecer el ID de las Imagenes de la Nota
     * @param imagesID ID de las Imagenes de la Nota
     */
    public void setImagesID(String imagesID) { this.imagesID = imagesID; }

    /**
     * Metodo para conseguir el ID de los Audios de la Nota
     * @return ID de los Audios de la Nota
     */
    public String getAudiosID() { return audiosID; }

    /**
     * Metodo para establecer el ID de los Audios de la Nota
     * @param audiosID ID de los Audios de la Nota
     */
    public void setAudiosID(String audiosID) { this.audiosID = audiosID; }

    /**
     * Metodo para saber si la Nota contiene Imagenes
     * @return Boolean sobre si tiene o no Imagenes
     */
    public Boolean getHaveImages() { return haveImages; }

    /**
     * Metodo para establecer si la Nota contiene Imagenes
     * @param haveImages Boolean sobre si tiene o no Imagenes
     */
    public void setHaveImages(Boolean haveImages) { this.haveImages = haveImages; }

    /**
     * Metodo para saber si la Nota contiene Documentos
     * @return Boolean sobre si tiene o no Documentos
     */
    public Boolean getHaveDocuments() { return haveDocuments; }

    /**
     * Metodo para establecer si la Nota contiene Documentos
     * @param haveDocuments Boolean sobre si tiene o no Documentos
     */
    public void setHaveDocuments(Boolean haveDocuments) { this.haveDocuments = haveDocuments; }

    /**
     * Metodo para saber si la Nota contiene Audios
     * @return Boolean sobre si tiene o no Audios
     */
    public Boolean getHaveAudios() { return haveAudios; }

    /**
     * Metodo para establecer si la Nota contiene Audios
     * @param haveAudios Boolean sobre si tiene o no Audios
     */
    public void setHaveAudios(Boolean haveAudios) { this.haveAudios = haveAudios; }

}
