package com.example.lize.workers;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import com.example.lize.R;

import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LogInActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    EditText email, password;
    Button login, signup;
    boolean isEmailValid, isPasswordValid;
    TextInputLayout emailError, passError;
    String patronPsw = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";

    //Relacionado con la Autentificación de FireBase
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        @Override
        protected void onCreate(Bundle savedInstanceState) {

           if (mAuth.getCurrentUser() != null){
                Toast.makeText(getApplicationContext(), "¡Bienvenido a LIZE!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            email = findViewById(R.id.email_header);
            password = findViewById(R.id.password_login);
            login = findViewById(R.id.login);
            signup = findViewById(R.id.signup);
            emailError = findViewById(R.id.emailError);
            passError = findViewById(R.id.passError);

            login.setOnClickListener(v -> {
                if (setValidation()) {
                    String email = this.email.getText().toString();
                    String password = this.password.getText().toString();

                    // SignIn process
                    mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "¡Bienvenido a LIZE!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, MainActivity.class);
                                startActivity(intent);

                            } else {    // Exception Procedure
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setPositiveButton("Aceptar", null);

                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException userException) {
                                    builder.setTitle("Autentificación inválida");
                                    builder.setMessage("Se ha producido un error de autentificación. \nEstá registrado en la App?");
                                    builder.create().show();

                                } catch( FirebaseAuthInvalidCredentialsException passwordExcept){
                                    builder.setTitle("Contraseña inválida");
                                    builder.setMessage("Se ha producido un error de credenciales. \nInténtelo de nuevo.");
                                    builder.create().show();

                                } catch (FirebaseTooManyRequestsException requestExcept) {
                                    builder.setTitle("Registro bloqueado");
                                    builder.setMessage("Se han producido demasiados intentos de autentificación. \nPor favor, inténtelo más tarde. ");
                                    builder.create().show();

                                } catch (Exception e) {
                                    Log.w(TAG, "Unidentified EXCEPTION. \n\tClass: " + e.getClass() + " \n\tMessage: " + e.getMessage());
                                }
                            }
                        });
                }
            });

            // Sign Up process
            signup.setOnClickListener(v -> {
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
            });

        }

        /**
         *  Metodo para validar el incio de sesión en la APP
         */
        public boolean setValidation() {
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

            } else if (password.getText().toString().matches(patronPsw)) {
                passError.setError(getResources().getString(R.string.error_incorrect_pwd));
                Toast.makeText(getApplicationContext(), passError.getError(), Toast.LENGTH_SHORT).show();
                isPasswordValid = false;

            } else  {
                isPasswordValid = true;
                passError.setErrorEnabled(false);
            }
            return isEmailValid && isPasswordValid;
        }

    /**
     * Método para guardar la nota en caso de que el usuario presione el botón atrás del móvil
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // Variables
            Toast toastReference = Toast.makeText(getBaseContext(), "Inicia sesión, por favor.", Toast.LENGTH_SHORT);
            toastReference.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    }
