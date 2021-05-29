package com.example.lize.workers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.lize.R;

import com.example.lize.models.MainViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;


public class AjustesActivity extends Activity {

    private ArrayList<String> info;
    private MainViewModel dataViewModel;
    private String name, surnames, email, psw, ambitos;
    private EditText editName, editSurnames, editPsw, editEmail;
    boolean isNameValid, areSurnamesValid, isEmailValid, isPasswordValid;
    TextInputLayout nameInput, apellidosInput, emailInput, pswInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getInfo();
        setInfoSettings();

        editName = (EditText) findViewById(R.id.nameSettings);
        editSurnames = (EditText) findViewById(R.id.surnamesSettings);
        editEmail = (EditText) findViewById(R.id.emailSettings);
        editPsw = (EditText) findViewById(R.id.pswSettings);
        nameInput = findViewById(R.id.nameInput);
        apellidosInput = findViewById(R.id.apellidosInput);
        emailInput = findViewById(R.id.emailInput);
        pswInput = findViewById(R.id.pswInput);

        MaterialToolbar topAppBar = findViewById(R.id.ambito_material_toolbar);
        topAppBar.setOnMenuItemClickListener(this::onMenuItemClick);

        Button guardarCambios = findViewById(R.id.guardar_cambios);
        guardarCambios.setOnClickListener(v -> {
            if (validarDatos()){
                /*
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Bundle userChanges = new Bundle();
                userChanges.putString("name", editName.getText().toString());
                userChanges.putString("surnames", editSurnames.getText().toString());
                userChanges.putString("email", editEmail.getText().toString());
                intent.putExtras(userChanges);
                setResult(RESULT_OK, intent);
                finish();*/
            }
        });
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
    //TODO numero de notas total
    private void initInfoViews(String name, String surnames, String email, String psw, String numAmbitos){
        EditText editName = (EditText) findViewById(R.id.nameSettings);
        EditText editSurnames = (EditText) findViewById(R.id.surnamesSettings);
        EditText editEmail = (EditText) findViewById(R.id.emailSettings);
        EditText editPsw = (EditText) findViewById(R.id.pswSettings);
        TextView numero_Ambitos = (TextView) findViewById(R.id.numAmbitos);
        TextView numero_Notas = (TextView) findViewById(R.id.numNotas);

        if (name != null) editName.setText(name);
        if (surnames != null)  editSurnames.setText(surnames);
        if (email != null)  editEmail.setText(email);
        if (psw != null)  editPsw.setText(psw);
        if (numAmbitos != null) numero_Ambitos.setText(numAmbitos);

    }

    private boolean validarDatos() {
        //Comprobaciones del nombre
        if (editName.getText().toString().isEmpty()) {
            nameInput.setError(getResources().getString(R.string.error_campo_vacio));
            Toast.makeText(getApplicationContext(), nameInput.getError(), Toast.LENGTH_SHORT).show();
            isNameValid = false;
        } else if (editName.getText().toString().length() >= 14) {
            nameInput.setError(getResources().getString(R.string.demasiados_caracteres));
            Toast.makeText(getApplicationContext(), nameInput.getError(), Toast.LENGTH_SHORT).show();
            isNameValid = false;
        } else  {
            isNameValid = true;
            //nameInput.setErrorEnabled(false);
        }

        //Comprobamos los apellidos
        if (editSurnames.getText().toString().isEmpty()) {
            apellidosInput.setError(getResources().getString(R.string.error_campo_vacio));
            Toast.makeText(getApplicationContext(), apellidosInput.getError(), Toast.LENGTH_SHORT).show();
            areSurnamesValid = false;
        } else if (editSurnames.getText().toString().length() >= 14) {
            apellidosInput.setError(getResources().getString(R.string.demasiados_caracteres));
            Toast.makeText(getApplicationContext(), apellidosInput.getError(), Toast.LENGTH_SHORT).show();
            areSurnamesValid = false;
        } else  {
            areSurnamesValid = true;
            apellidosInput.setErrorEnabled(false);
        }

        // Comprobaciones del email
        if (editEmail.getText().toString().isEmpty()) {
            emailInput.setError(getResources().getString(R.string.error_campo_vacio));
            Toast.makeText(getApplicationContext(), emailInput.getError(), Toast.LENGTH_SHORT).show();
            isEmailValid = false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString()).matches()) {
            emailInput.setError(getResources().getString(R.string.error_invalid_email));
            Toast.makeText(getApplicationContext(), emailInput.getError(), Toast.LENGTH_SHORT).show();
            isEmailValid = false;

        } else  {
            isEmailValid = true;
            emailInput.setErrorEnabled(false);
        }

        // Validamos la contraseña
        if (editPsw.getText().toString().isEmpty()) {
            pswInput.setError(getResources().getString(R.string.error_campo_vacio));
            Toast.makeText(getApplicationContext(), pswInput.getError(), Toast.LENGTH_SHORT).show();
            isPasswordValid = false;

        } else if (editPsw.getText().length() < 8) {
            pswInput.setError(getResources().getString(R.string.error_invalid_pwd_Login));
            Toast.makeText(getApplicationContext(), pswInput.getError(), Toast.LENGTH_SHORT).show();
            isPasswordValid = false;

        } else  {
            isPasswordValid = true;
            pswInput.setErrorEnabled(false);
        }

        if (isNameValid && areSurnamesValid && isEmailValid && isPasswordValid) {
            Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
            //dataViewModel.getUserSelected().getValue().setFirst(editName.getText().toString());
            //dataViewModel.getUserSelected().getValue().setFirst(editSurnames.getText().toString());
            //dataViewModel.getUserSelected().getValue().setFirst(editEmail.getText().toString());
            //dataViewModel.getUserSelected().getValue().setFirst(editPsw.getText().toString());
            return true;
        }
        return false;
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
