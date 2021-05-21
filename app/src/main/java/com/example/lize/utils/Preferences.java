package com.example.lize.utils;

import android.content.res.Resources.Theme;
import android.view.ContextThemeWrapper;

import com.example.lize.R;

public class Preferences {
    private static int theme = R.style.Theme_Red;


    public static void setTheme(int selectedTheme){
        switch (selectedTheme){
            case 1:     theme = R.style.Theme_Red; break;
            case 2:     theme = R.style.Theme_Purple; break;
            case 3:     theme = R.style.Theme_Indigo; break;
            case 4:     theme = R.style.Theme_Blue; break;
            case 5:     theme = R.style.Theme_Teal; break;
            case 6:     theme = R.style.Theme_Green; break;
            case 7:     theme = R.style.Theme_Yellow; break;
            case 8:     theme = R.style.Theme_Orange; break;
            case 9:     theme = R.style.Theme_Brown; break;
        }
    }




    public static void applyTheme(ContextThemeWrapper contextThemeWrapper) {

        contextThemeWrapper.setTheme(theme);
    }

}
