package com.example.lize.utils;

import android.view.ContextThemeWrapper;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.lize.R;

public class Preferences {

    private static int selectedTheme = R.style.Theme_Red;
    private static int selectedColor;

    /**
     * Metodo para conseguir un Theme
     * @param color Color del Theme que queremos
     * @return Theme resultante
     */
    public static int getTheme(int color) {
        switch (color){
            case 1:     return R.style.Theme_Red;
            case 2:     return R.style.Theme_Purple;
            case 3:     return R.style.Theme_Indigo;
            case 4:     return R.style.Theme_Blue;
            case 5:     return R.style.Theme_Teal;
            case 6:     return R.style.Theme_Green;
            case 7:     return R.style.Theme_Yellow;
            case 8:     return R.style.Theme_Orange;
            case 9:     return R.style.Theme_Brown;
        }
        return selectedTheme;
    }

    /**
     * Metodo para establecer el Theme
     * @param color Color del Theme a establecer
     */
    public static void setSelectedTheme(int color) {
        selectedColor = color;
        switch (selectedColor){
            case 1:     selectedTheme = R.style.Theme_Red; break;
            case 2:     selectedTheme = R.style.Theme_Purple; break;
            case 3:     selectedTheme = R.style.Theme_Indigo; break;
            case 4:     selectedTheme = R.style.Theme_Blue; break;
            case 5:     selectedTheme = R.style.Theme_Teal; break;
            case 6:     selectedTheme = R.style.Theme_Green; break;
            case 7:     selectedTheme = R.style.Theme_Yellow; break;
            case 8:     selectedTheme = R.style.Theme_Orange; break;
            case 9:     selectedTheme = R.style.Theme_Brown; break;
        }
    }

    /**
     * Metodo para conseguir el color seleccionado
     * @return Color seleccionado
     */
    public static int getSelectedColor() { return selectedColor; }

    /**
     * Metodo para aplicar el Theme seleccionado
     * @param contextThemeWrapper Theme a aplicar
     */
    public static void applySelectedTheme(ContextThemeWrapper contextThemeWrapper) { contextThemeWrapper.setTheme(selectedTheme); }

    /**
     * Metodo para conseguir el color del Ambito
     * @param ambitoColor Color del Ambito
     * @return Theme del color
     */
    public static int getAmbitoColor(int ambitoColor) {
        if(ambitoColor == selectedColor){
        switch (ambitoColor) {
            case 1:  return R.color.Ambito_Red;
            case 2:  return R.color.Ambito_Purple;
            case 3:  return R.color.Ambito_Indigo;
            case 4:  return R.color.Ambito_Blue;
            case 5:  return R.color.Ambito_Teal;
            case 6:  return R.color.Ambito_Green;
            case 7:  return R.color.Ambito_Yellow;
            case 8:  return R.color.Ambito_Orange;
            case 9:  return R.color.Ambito_Brown;
            default: return R.color.Ambito_Red;
            }
        } else {
             if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) return R.color.md_grey_dark;
             else return R.color.md_grey_100;
        }
    }

    /**
     * Metodo para conseguir el color del Ambito seleccionado
     * @param ambitoColor Color del Ambito seleccionado
     * @return Theme del Ambito
     */
    public static int getAmbitoPressedColor(int ambitoColor){

        switch (ambitoColor){
            case 1: return  R.color.Presseed_Red;
            case 2: return R.color.Presseed_Purple;
            case 3: return R.color.Presseed_Indigo;
            case 4: return R.color.Presseed_Blue;
            case 5: return R.color.Presseed_Teal;
            case 6: return R.color.Presseed_Green;
            case 7: return R.color.Presseed_Yellow;
            case 8: return R.color.Presseed_Orange;
            case 9: return R.color.Presseed_Brown;
            default: return  R.color.Presseed_Red;
        }
    }

    /**
     * Metodo para conseguir el color del Ambito Default
     * @return Color Default
     */
    public static int getDefaultAmbitoColor(){ return R.color.white; }
}
