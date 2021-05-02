package com.example.lize.workers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lize.R;
import com.example.lize.data.Ambito;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

public class NewAmbitoActivity extends AppCompatActivity {

    private EditText nombre;
    private int colorAmbito;
    private TextInputLayout nombreError, colorAmbitoError;
    boolean isNameValid, isColorAmbitoValid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ambito);

        colorAmbito = 0;
        nombre = findViewById(R.id.nombre);
        colorAmbitoError = findViewById(R.id.colorAmbitoError);
        nombreError = findViewById(R.id.nameError);

        MaterialToolbar topAppBar = findViewById(R.id.ambito_material_toolbar);
        topAppBar.setOnMenuItemClickListener(this::onMenuItemClick);

        Button newAmbito = findViewById(R.id.newAmbitoButton);
        newAmbito.setOnClickListener(v -> {
            if (validarDatos()){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Bundle ambito = new Bundle();
                ambito.putString("name", nombre.getText().toString());
                ambito.putLong("color", colorAmbito);
                intent.putExtras(ambito);
                setResult(RESULT_OK, intent);
                finish();
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
        if(colorAmbito == 0){
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


    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cardRojo:         colorAmbito = 1;  break;
            case R.id.cardMorado:       colorAmbito = 2;   break;
            case R.id.cardIndigo:       colorAmbito = 3;      break;
            case R.id.cardAzul:         colorAmbito = 4;     break;
            case R.id.cardTeal:         colorAmbito = 5;   break;
            case R.id.cardVerde:        colorAmbito = 6;      break;
            case R.id.cardAmarillo:     colorAmbito = 7;    break;
            case R.id.cardNaranja:      colorAmbito = 8;      break;
            case R.id.cardMarron:       colorAmbito = 9;   break;

            //case R.id.btnIcoAtras:      finish();                                           break;
        }
    }

    /**
     * Implementación del método OnMenuItemClick para definir las acciones de los items del Toolbar.
     * @param item item del Toolbar: go back(arrow)
     */
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