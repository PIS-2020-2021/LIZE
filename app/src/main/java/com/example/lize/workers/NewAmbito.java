package com.example.lize.workers;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lize.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

public class NewAmbito extends AppCompatActivity {

    private EditText nombre;
    private String colorAmbito;
    private TextInputLayout nombreError, colorAmbitoError;
    boolean isNameValid, isColorAmbitoValid;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ambito);

        colorAmbito = null;
        nombre = findViewById(R.id.nombre);
        colorAmbitoError = findViewById(R.id.colorAmbitoError);
        nombreError = findViewById(R.id.nameError);

        MaterialToolbar topAppBar = findViewById(R.id.ambito_material_toolbar);
        topAppBar.setOnMenuItemClickListener(this::onMenuItemClick);

        Button newAmbito = findViewById(R.id.newAmbitoButton);
        newAmbito.setOnClickListener(v -> {
            if (validarDatos()){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validarDatos() {
        //Comprobaciones de Los Nombres
        if (nombre.getText().toString().isEmpty()) {
            nombreError.setError(getResources().getString(R.string.error_campo_vacio));
            Toast.makeText(getApplicationContext(), nombreError.getError(), Toast.LENGTH_SHORT).show();
            isNameValid = false;
        } else if (nombre.getText().toString().length() >= 14) {
            nombreError.setError(getResources().getString(R.string.demasiados_caracteres));
            Toast.makeText(getApplicationContext(), nombreError.getError(), Toast.LENGTH_SHORT).show();
            isNameValid = false;
        } else  {
            isNameValid = true;
            nombreError.setErrorEnabled(false);
        }

        //Comprobamos que se ha seleccionado algún Color
        if(colorAmbito == null){
            colorAmbitoError.setError(getResources().getString(R.string.errorColorAmbito));
            Toast.makeText(getApplicationContext(), colorAmbitoError.getError(), Toast.LENGTH_SHORT).show();
            isColorAmbitoValid = false;
        } else {
            isColorAmbitoValid = true;
            colorAmbitoError.setErrorEnabled(false);
        }

        if (isNameValid && isColorAmbitoValid) {
            Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cardRojo:         colorAmbito = "Red";  break;
            case R.id.cardMorado:       colorAmbito = "Purple";   break;
            case R.id.cardIndigo:       colorAmbito = "Indigo";      break;
            case R.id.cardAzul:         colorAmbito = "Blue";     break;
            case R.id.cardTeal:         colorAmbito = "Teal";   break;
            case R.id.cardVerde:        colorAmbito = "Green";      break;
            case R.id.cardAmarillo:     colorAmbito = "Yellow";    break;
            case R.id.cardNaranja:      colorAmbito = "Orange";      break;
            case R.id.cardMarron:       colorAmbito = "Brown";   break;

            //case R.id.btnIcoAtras:      finish();                                           break;
        }
    }

    /**
     * Implementación del método OnMenuItemClick para definir las acciones de los items del Toolbar.
     * @param item item del Toolbar: go back(arrow)
     */
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.arrow) {
            onBackPressed();
        } else {
            return false;
        }
        return true;
    }


}