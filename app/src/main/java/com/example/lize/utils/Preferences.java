package com.example.lize.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.view.ContextThemeWrapper;

import com.example.lize.R;

public class Preferences {

    private static int selectedTheme = R.style.Theme_Red;
    private static int selectedAmbito;

    public static int getTheme(int color){
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

    public static void setSelectedTheme(int color){
        switch (color){
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
        selectedAmbito = color;
    }

    public static int getSelectedTheme() {
        return selectedTheme;
    }

    public static void applySelectedTheme(ContextThemeWrapper contextThemeWrapper) {
        contextThemeWrapper.setTheme(selectedTheme);
    }

    public static int getAmbitoColor(int ambitoColor) {
        if (ambitoColor == selectedAmbito) {
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
            return R.color.white;
        }
    }

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


    public static int getDefaultAmbitoColor(){
        return R.color.white;
    }
}
