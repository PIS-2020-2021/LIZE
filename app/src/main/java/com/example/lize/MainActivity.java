package com.example.lize;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    //Declaramos Títulos para nuestro Navigation Drawer List View
    String AMBITOS[] = {"Home", "Eventos", "Trabajo", "Universidad"};

    //Creamos un recurso String para el nombre y el eMail para el HeaderView
    //También creamos un recurso para la imagen de perfil del HeaderView
    String NAME = "Adrian Saiz";
    String EMAIL = "adrian.saizdepedro@soyunemail.com";
    int IMG_PROFILE = R.drawable.adrian_image;


    //Declaramos el toolBar Object
    private Toolbar toolbar;

    //Declaramos RecyclerView
    RecyclerView mRecyclerView;
    //Declaramos un Adapter par el Recycler View
    RecyclerView.Adapter mAdapter;
    //Declaramos un LayoutManager como Linear Layout Manager
    RecyclerView.LayoutManager mLayoutManager;
    //Declaramos un DrawerLayout
    DrawerLayout drawerLayout;

    //Declaramos un Action Bar Drawer Toggle
    ActionBarDrawerToggle mDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Asignamos el objeto toolBar de la view
        y después configuramos la Action Bar a nuestro ToolBar */
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //Asignamos el RecyclerView Object al xmlView
        mRecyclerView = findViewById(R.id.recyclerView);
        //Hacemos saber al sistema que la lista de objetos es fija
        //mRecyclerView.setHasFixedSize(true);

        //Creamos el Adaptador de MyAdapter class
        //Le pasamos los ambitos, iconos (si hubiera), nombre/mail del header e imagen del header
        mAdapter = new MyAdapter(AMBITOS, NAME, EMAIL, IMG_PROFILE);

        //Configuramos el adapter al RecyclerView
        mRecyclerView.setAdapter(mAdapter);

        //Creamos un LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        //Configuramos el LinearLayoutManager
        mRecyclerView.setLayoutManager(mLayoutManager);


        //Asignamos el Drawer Object a la view
        drawerLayout = findViewById(R.id.drawerLayout);
        //Creamos el DrawerToggle Object
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer){
            @Override
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                //Si quisieramos que pasase algo cuando abramos el drawer, esta es la función
            }

            @Override
            public void onDrawerClosed(View drawerView){
                super.onDrawerClosed(drawerView);
                //Si quisieramos que pasase algo cuando cerremos el drawer, esta es la función
            }
        };

        //Asignamos el Drawer Listener a Drawer Toggle
        drawerLayout.setDrawerListener(mDrawerToggle);
        //Configuramos el DrawerToggle par que sincronice con el Estado
        mDrawerToggle.syncState();

    }

}