package com.example.lize.workers;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Patterns;
import com.example.lize.R;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {
    // Variables
    EditText email, password;
    Button login, signup;
    boolean isEmailValid, isPasswordValid;
    TextInputLayout emailError, passError;

    //Relacionado con la Autentificación de FireBase
    private FirebaseAuth mAuth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            email = findViewById(R.id.email);
            password = findViewById(R.id.password_login);
            login = findViewById(R.id.login);
            signup = findViewById(R.id.signup);
            emailError = findViewById(R.id.emailError);
            passError = findViewById(R.id.passError);

            login.setOnClickListener(v -> {
                if (SetValidation()) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(),
                            password.getText().toString()).addOnCompleteListener(a -> {
                                if(a.isSuccessful()){
                                    Intent intent = new Intent(this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    showAlert();
                                }
                    });
                }
            });

            signup.setOnClickListener(v -> {
                // Vamos a crear un nuevo usuario
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
            });

        }

        /**
         *  Metodo para validar el incio de sesión en la APP
         */
        public boolean SetValidation() {

            // Primero validamos el email
            if (email.getText().toString().isEmpty()) {
                emailError.setError(getResources().getString(R.string.error_campo_vacio));
                Toast.makeText(getApplicationContext(), emailError.getError(), Toast.LENGTH_SHORT).show();
                isEmailValid = false;

            } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                emailError.setError(getResources().getString(R.string.error_invalid_email));
                Toast.makeText(getApplicationContext(), emailError.getError(), Toast.LENGTH_SHORT).show();
                isEmailValid = false;

            } else  {
                isEmailValid = true;
                emailError.setErrorEnabled(false);
            }

            // Ahora validamos la contraseña
            if (password.getText().toString().isEmpty()) {
                passError.setError(getResources().getString(R.string.error_campo_vacio));
                Toast.makeText(getApplicationContext(), passError.getError(), Toast.LENGTH_SHORT).show();
                isPasswordValid = false;

            } else if (password.getText().length() < 8) {
                passError.setError(getResources().getString(R.string.error_invalid_pwd));
                Toast.makeText(getApplicationContext(), passError.getError(), Toast.LENGTH_SHORT).show();
                isPasswordValid = false;

            } else  {
                isPasswordValid = true;
                passError.setErrorEnabled(false);
            }

            if (isEmailValid && isPasswordValid) {
                Toast.makeText(getApplicationContext(), "¡Bienvenido a LIZE!", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        }

        //FireBase Alert
        private void showAlert(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("Se ha producido un error autenticando al usuario\n ¿Está Registrado en la App?");
            builder.setPositiveButton("Aceptar", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }


    }
