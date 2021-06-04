package com.example.lize.workers;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
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

public class NewAmbitoActivity extends AppCompatActivity {
    private EditText nombre;
    private int colorAmbito = 0;
    private TextInputLayout nombreError, colorAmbitoError;
    private ArrayList<Integer> colors;
    private CardView red, morado, indigo, azul, teal, verde, amarillo, naranja, marron;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.applySelectedTheme(this);
        setContentView(R.layout.activity_new_ambito);

        getColors();

        red = findViewById(R.id.cardRojo);
        morado = findViewById(R.id.cardMorado);
        indigo = findViewById(R.id.cardIndigo);
        azul = findViewById(R.id.cardAzul);
        teal = findViewById(R.id.cardTeal);
        verde = findViewById(R.id.cardVerde);
        amarillo = findViewById(R.id.cardAmarillo);
        naranja = findViewById(R.id.cardNaranja);
        marron = findViewById(R.id.cardMarron);

        setColorOfCardViews();

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

    /**
     * Metodo para validar los Datos introducidos
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
     * Método para guardar la nota en caso de que el usuario presione el botón atrás del móvil
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            setResult(RESULT_CANCELED, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Metodo para conseguir los colores de los Ambitos ya creados
     */
    private void getColors() {
        Intent intent = getIntent();
        this.colors = intent.getIntegerArrayListExtra("Ambitos");
    }

    /**
     * Metodo para establecer el color seleccionado al nuevo Ambito
     * @param color Color seleccionado
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setColorSelected(int color){
        if (colors.contains(color)) colorAmbitoError.setError(getResources().getString(R.string.errorColorAmbitoAlredySelected));
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
     * Metodo par conseguir las CardViews segun su color
     * @param colorAmbito Color del nuevo Ambito
     * @return CardViews deseadas
     */
    private CardView getCardViewByColor(int colorAmbito){
        switch (colorAmbito){
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
     * Metodo para establecer los posibles colores de las CardViews
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setColorOfCardViews(){
        red.setCardBackgroundColor(getColor(R.color.Default_Red));
        morado.setCardBackgroundColor(getColor(R.color.Default_Purple));
        indigo.setCardBackgroundColor(getColor(R.color.Default_Indigo));
        azul.setCardBackgroundColor(getColor(R.color.Default_Blue));
        teal.setCardBackgroundColor(getColor(R.color.Default_Teal));
        verde.setCardBackgroundColor(getColor(R.color.Default_Green));
        amarillo.setCardBackgroundColor(getColor(R.color.Default_Yellow));
        naranja.setCardBackgroundColor(getColor(R.color.Default_Orange));
        marron.setCardBackgroundColor(getColor(R.color.Default_Brown));

        try {
            for (int col : colors) {
                CardView cardView = getCardViewByColor(col);
                if (cardView != null ) cardView.setCardBackgroundColor(getColor(R.color.Default_Grey));
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error al cargar la lista de colores ya seleccionados", Toast.LENGTH_SHORT).show();
        }

        switch (colorAmbito) {
            case 1:     red.setCardBackgroundColor(getColor(R.color.Presseed_Red)); break;
            case 2:     morado.setCardBackgroundColor(getColor(R.color.Presseed_Purple)); break;
            case 3:     indigo.setCardBackgroundColor(getColor(R.color.Presseed_Indigo)); break;
            case 4:     azul.setCardBackgroundColor(getColor(R.color.Presseed_Blue)); break;
            case 5:     teal.setCardBackgroundColor(getColor(R.color.Presseed_Teal)); break;
            case 6:     verde.setCardBackgroundColor(getColor(R.color.Presseed_Green)); break;
            case 7:     amarillo.setCardBackgroundColor(getColor(R.color.Presseed_Yellow)); break;
            case 8:     naranja.setCardBackgroundColor(getColor(R.color.Presseed_Orange)); break;
            case 9:     marron.setCardBackgroundColor(getColor(R.color.Presseed_Brown)); break;
            default:
        }
    }

    /**
     * Metodo para conseguir establecer el color seleccionado segun el View clickeado
     * @param view View clickeado
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cardRojo:         setColorSelected(1);    break;
            case R.id.cardMorado:       setColorSelected(2);    break;
            case R.id.cardIndigo:       setColorSelected(3);    break;
            case R.id.cardAzul:         setColorSelected(4);    break;
            case R.id.cardTeal:         setColorSelected(5);    break;
            case R.id.cardVerde:        setColorSelected(6);    break;
            case R.id.cardAmarillo:     setColorSelected(7);    break;
            case R.id.cardNaranja:      setColorSelected(8);    break;
            case R.id.cardMarron:       setColorSelected(9);    break;
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