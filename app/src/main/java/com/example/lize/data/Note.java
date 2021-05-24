package com.example.lize.data;

import android.net.Uri;

import java.util.ArrayList;

// Note Model Class
public class Note {

    private String title;
    private String text_plain;
    private String text_html;
    private String selfID;
    private String ambitoID;
    private String folderTAG;

    private String documentsID;
    private String imagesID;
    private Boolean haveImages;
    private Boolean haveDocuments;

    public Note(String title, String text_plain, String text_html) {
        this.title = title;
        this.text_plain = text_plain;
        this.text_html = text_html;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) { this.title = title; }

    public String getText_plain() {
        return text_plain;
    }

    public void setText_plain(String text_plain) { this.text_plain = text_plain; }

    public String getAmbitoID() {
        return ambitoID;
    }

    public void setAmbitoID(String ambitoID) {
        this.ambitoID = ambitoID;
    }

    public String getFolderTAG() {
        return folderTAG;
    }

    public void setFolderTAG(String folderTAG) {
        this.folderTAG = folderTAG;
    }

    public String getSelfID() { return selfID; }

    public void setSelfID(String selfID) { this.selfID = selfID; }

    public String getText_html() {
        return text_html;
    }

    public void setText_html(String text_html) {
        this.text_html = text_html;
    }

    public Boolean getHaveImages() {
        return haveImages;
    }

    public void setHaveImages(Boolean haveImages) {
        this.haveImages = haveImages;
    }

    public Boolean getHaveDocuments() {
        return haveDocuments;
    }

    public void setHaveDocuments(Boolean haveDocuments) {
        this.haveDocuments = haveDocuments;
    }

    public void setDocumentsID(String documentsID) {
        this.documentsID = documentsID;
    }
    public String getDocumentsID() {
        return documentsID;
    }

    public String getImagesID() {
        return imagesID;
    }

    public void setImagesID(String imagesID) {
        this.imagesID = imagesID;
    }
}
