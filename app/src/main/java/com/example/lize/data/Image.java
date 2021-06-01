package com.example.lize.data;

import java.io.File;

public class Image extends File {
    private String id;

    /**
     * Constructor de la clase con dos parámetros
     * @param cacheDir Directorio de la Cache
     * @param valueOf Valor de la Imagen
     */
    public Image(File cacheDir, String valueOf) {
        super(cacheDir,valueOf);
    }

    /**
     * Metodo para conseguir el ID de la Imagen
     * @return ID de la Imagen
     */
    public String getId() { return id; }

    /**
     * Metodo para establecer el ID de la Imgen
     * @param id ID de la Imagen
     */
    public void setId(String id) { this.id = id; }

}
