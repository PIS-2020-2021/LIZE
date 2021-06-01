package com.example.lize.workers;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lize.R;
import com.example.lize.utils.Preferences;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class EditAmbitoActivity extends AppCompatActivity {

    private EditText nombre;
    private CardView red, morado, indigo, azul, teal, verde, amarillo, naranja, marron;
    private TextInputLayout nombreError, colorAmbitoError;

    private ArrayList<Integer> colors;
    private int colorAmbito;
    private String oldname;
    private String selfID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.applySelectedTheme(this);
        setContentView(R.layout.activity_edit_ambito);

        getValues();

        red = findViewById(R.id.cardRojoEdit);
        morado = findViewById(R.id.cardMoradoEdit);
        indigo = findViewById(R.id.cardIndigoEdit);
        azul = findViewById(R.id.cardAzulEdit);
        teal = findViewById(R.id.cardTealEdit);
        verde = findViewById(R.id.cardVerdeEdit);
        amarillo = findViewById(R.id.cardAmarilloEdit);
        naranja = findViewById(R.id.cardNaranjaEdit);
        marron = findViewById(R.id.cardMarronEdit);

        setColorOfCardViews();

        nombre = findViewById(R.id.nombre);
        colorAmbitoError = findViewById(R.id.colorAmbitoError);
        nombreError = findViewById(R.id.nameError);

        nombre.setText(oldname);

        MaterialToolbar topAppBar = findViewById(R.id.ambito_material_toolbar);
        topAppBar.setOnMenuItemClickListener(this::onMenuItemClick);

        Button newAmbito = findViewById(R.id.editAmbitoButton);
        newAmbito.setOnClickListener(v -> {
            if (validarDatos()) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Bundle ambito = new Bundle();
                ambito.putString("name", nombre.getText().toString());
                ambito.putLong("color", colorAmbito);
                ambito.putString("selfID", selfID);
                intent.putExtras(ambito);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * Metodo para validar los Datos
     * @return True si pasa todos los filtros, False si no
     */
    private boolean validarDatos() {
        boolean isNameValid, isColorAmbitoValid;
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
        if (colorAmbito == 0) {
            colorAmbitoError.setError(getResources().getString(R.string.errorColorAmbitoNoSelection));
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

    /**
     * Metodo para conseguir los valores del Ambito a editar
     */
    private void getValues() {
        Intent intent = getIntent();
        this.colors = intent.getIntegerArrayListExtra("Ambitos");
        this.oldname = intent.getStringExtra("OldName");
        this.colorAmbito = intent.getIntExtra("OldColor", 0);
        this.selfID = intent.getStringExtra("SelfID");
        this.colors.remove(Integer.valueOf(this.colorAmbito));
    }

    /**
     * Metodo para establecer el color seleccioando
     * @param color Color seleccionado
     */
    private void setColorSelected(int color){
        if(colors.contains(color)) colorAmbitoError.setError(getResources().getString(R.string.errorColorAmbitoAlredySelected));
        else {
            if (colorAmbito == color) colorAmbito = 0;
            else {
                CardView cardView = getCardViewByColor(color);
                Animation animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
                if (cardView != null) cardView.startAnimation(animZoomOut);
                colorAmbito = color;
                colorAmbitoError.setError("");
            }
        }
        setColorOfCardViews();
    }

    /**
     * Metodo para conseguir el CardView segun el Color seleccionado
     * @param colorAmbito Color del Ambito
     * @return CardView del Ambito
     */
    private CardView getCardViewByColor(int colorAmbito) {
        switch (colorAmbito) {
            case 1:     return this.red;
            case 2:     return this.morado;
            case 3:     return this.indigo;
            case 4:     return this.azul;
            case 5:     return this.teal;
            case 6:     return this.verde;
            case 7:     return this.amarillo;
            case 8:     return this.naranja;
            case 9:     return this.marron;
            default:    return null;
        }
    }

    /**
     * Metodo para establecer los colores del Background de las Cards
     */
    public void setColorOfCardViews(){
        red.setCardBackgroundColor(getResources().getColor(R.color.Default_Red));
        morado.setCardBackgroundColor(getResources().getColor(R.color.Default_Purple));
        indigo.setCardBackgroundColor(getResources().getColor(R.color.Default_Indigo));
        azul.setCardBackgroundColor(getResources().getColor(R.color.Default_Blue));
        teal.setCardBackgroundColor(getResources().getColor(R.color.Default_Teal));
        verde.setCardBackgroundColor(getResources().getColor(R.color.Default_Green));
        amarillo.setCardBackgroundColor(getResources().getColor(R.color.Default_Yellow));
        naranja.setCardBackgroundColor(getResources().getColor(R.color.Default_Orange));
        marron.setCardBackgroundColor(getResources().getColor(R.color.Default_Brown));

        try {
            for (int col : colors) {
                CardView cardView = getCardViewByColor(col);
                if (cardView != null) cardView.setCardBackgroundColor(getResources().getColor(R.color.Default_Grey));
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error al cargar la lista de colores ya seleccionados", Toast.LENGTH_SHORT).show();
        }

        switch (colorAmbito) {
            case 1:     red.setCardBackgroundColor(getResources().getColor(R.color.Presseed_Red)); break;
            case 2:     morado.setCardBackgroundColor(getResources().getColor(R.color.Presseed_Purple)); break;
            case 3:     indigo.setCardBackgroundColor(getResources().getColor(R.color.Presseed_Indigo)); break;
            case 4:     azul.setCardBackgroundColor(getResources().getColor(R.color.Presseed_Blue)); break;
            case 5:     teal.setCardBackgroundColor(getResources().getColor(R.color.Presseed_Teal)); break;
            case 6:     verde.setCardBackgroundColor(getResources().getColor(R.color.Presseed_Green)); break;
            case 7:     amarillo.setCardBackgroundColor(getResources().getColor(R.color.Presseed_Yellow)); break;
            case 8:     naranja.setCardBackgroundColor(getResources().getColor(R.color.Presseed_Orange)); break;
            case 9:     marron.setCardBackgroundColor(getResources().getColor(R.color.Presseed_Brown)); break;
            default:
        }
    }

    /**
     * Metodo para conseguir establecer el color seleccionado segun el View clickeado
     * @param view View clickeado
     */
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cardRojoEdit:         setColorSelected(1);    break;
            case R.id.cardMoradoEdit:       setColorSelected(2);    break;
            case R.id.cardIndigoEdit:       setColorSelected(3);    break;
            case R.id.cardAzulEdit:         setColorSelected(4);    break;
            case R.id.cardTealEdit:         setColorSelected(5);    break;
            case R.id.cardVerdeEdit:        setColorSelected(6);    break;
            case R.id.cardAmarilloEdit:     setColorSelected(7);    break;
            case R.id.cardNaranjaEdit:      setColorSelected(8);    break;
            case R.id.cardMarronEdit:       setColorSelected(9);    break;
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
        } else return false;
        return true;
    }
}