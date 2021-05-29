package com.example.lize.workers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;

import com.example.lize.R;
import com.example.lize.models.MainViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;


public class AjustesActivity extends Activity {

    private MainViewModel dataViewModel;
    private Button guardarCambios;
    private ArrayList<String> info;
    private String name, surnames, email, psw, ambitos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getInfo();
        setInfoSettings();


        //dataViewModel.getUserSelected().observe((LifecycleOwner) this, user -> initInfoViews(name, surnames, email, psw, ambitos));


        MaterialToolbar topAppBar = findViewById(R.id.ambito_material_toolbar);
        topAppBar.setOnMenuItemClickListener(this::onMenuItemClick);



    }

    private void getInfo(){
        Intent intent = getIntent();
        this.info = intent.getStringArrayListExtra("Info_User");
        this.name = info.get(0);
        this.surnames = info.get(1);
        this.email = info.get(2);
        this.psw = info.get(3);
        this.ambitos = info.get(4);

    }

    private void setInfoSettings () {
        initInfoViews(name, surnames, email, psw, ambitos);
        TextView userFullName = (TextView) findViewById(R.id.user_full_name);
        String fullName = name + ' ' + surnames;
        if (name != null) userFullName.setText(fullName);
    }

    /**
     * Setea el Header de la MainActivity
     * @param name nombre del User
     * @param surnames apellidos del User
     * @param email email del User
     * @param psw contraseña del User
     * @param numAmbitos Numero de ámbitos creados por el User
     */
    private void initInfoViews(String name, String surnames, String email, String psw, String numAmbitos){
        EditText editName = (EditText) findViewById(R.id.nameSettings);
        EditText editSurnames = (EditText) findViewById(R.id.surnamesSettings);
        EditText editEmail = (EditText) findViewById(R.id.emailSettings);
        EditText editPsw = (EditText) findViewById(R.id.pswSettings);
        TextView numero_Ambitos = (TextView) findViewById(R.id.numAmbitos);

        if (name != null) editName.setText(name);
        if (surnames != null)  editSurnames.setText(surnames);
        if (email != null)  editEmail.setText(email);
        if (psw != null)  editPsw.setText(psw);
        if (numAmbitos != null) numero_Ambitos.setText(numAmbitos);
    }

    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.arrow) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            setResult(RESULT_CANCELED, intent);
            finish();
        } else {
            return false;
        }
        return true;
    }
}
