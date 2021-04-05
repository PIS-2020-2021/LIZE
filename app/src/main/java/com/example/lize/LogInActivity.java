package com.example.lize;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;

public class LogInActivity extends AppCompatActivity {
    // Variables
    EditText email, password;
    Button btn_login;
    TextView register;
    boolean isEmailValid, isPasswordValid;
    TextInputLayout emailError, passError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        emailError = findViewById(R.id.emailError);
        passError = findViewById(R.id.passError);

        btn_login.setOnClickListener(v -> SetValidation());

        register.setOnClickListener(v -> {
            // Vamos a crear un nuevo usuario
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        });
    }

    /**
     *  Metodo para validar el incio de sesión en la APP
     */
    public void SetValidation() {

        // Primero validamos el email
        if (email.getText().toString().isEmpty()) {
            emailError.setError(getResources().getString(R.string.error_campo_vacio));
            isEmailValid = false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            emailError.setError(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;

        } else  {
            isEmailValid = true;
            emailError.setErrorEnabled(false);
        }

        // Ahora validamos la contraseña
        if (password.getText().toString().isEmpty()) {
            passError.setError(getResources().getString(R.string.error_campo_vacio));
            isPasswordValid = false;

        } else if (password.getText().length() < 8) {
            passError.setError(getResources().getString(R.string.error_invalid_pwd));
            isPasswordValid = false;

        } else  {
            isPasswordValid = true;
            passError.setErrorEnabled(false);
        }

        if (isEmailValid && isPasswordValid) {
            Toast.makeText(getApplicationContext(), "¡Bienvenido a LIZE!", Toast.LENGTH_SHORT).show();
        }

    }

}