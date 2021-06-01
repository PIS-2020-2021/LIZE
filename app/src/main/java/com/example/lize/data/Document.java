package com.example.lize.data;

import android.net.Uri;
import java.io.File;

public class Document {
    private String name;
    private final Uri Url;
    private String id;
    private String path;
    private byte[] bytes;
    File content;

    /**
     * Constructor de la clase con un parametro
     * @param uri Uri del documento
     */
    public Document(Uri uri){
        this.Url = uri;
    }

    /**
     * Metodo para conseguir el nombre del Documento
     * @return Nombre del Documento
     */
    public String getName() {
        return name;
    }

    /**
     * Metodo para establecer el nombre del Documento
     * @param name Nombre del Documento
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Metodo para conseguir la URL del Documento
     * @return URL del Documento
     */
    public Uri getUrl() {
        return Url;
    }

    /**
     * Metodo para conseguir el ID del Documento
     * @return ID del Documento
     */
    public String getId() {
        return id;
    }

    /**
     * Metodo para establecer el ID del Documento
     * @param id ID del Documento
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Metodo para conseguir el PATH del Documento
     * @return PATH del Documento
     */
    public String getPath() {
        return path;
    }

    /**
     * Metodo para establecer el PATH del Documento
     * @param path PATH del Documento
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Metodo para conseguir los Bytes que ocupa el Documento
     * @return Bytes del Documento
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Metodo para establecer el tamaño del Documento
     * @param bytes Tamaño del Documento
     */
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Metodo para conseguir el contenido del Documento
     * @return Contenido del Documento
     */
    public File getContent() {
        return content;
    }

    /**
     * Metodo para establecer el contenido del Documento
     * @param content Contenido del Documento
     */
    public void setContent(File content) {
        this.content = content;
    }
}
