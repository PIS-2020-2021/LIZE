package com.example.lize.workers;

import android.os.Bundle;
import android.content.Intent;
import android.util.Patterns;

import com.example.lize.adapters.DatabaseAdapter;
import com.example.lize.data.User;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import com.example.lize.R;
import com.google.firebase.auth.FirebaseAuth;

import android.widget.Toast;
import android.widget.EditText;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
        // Variables
        Button signup;
        TextView login;
        EditText nombre, apellidos, email, psw, pswCheck;
        boolean isNameValid, isEmailValid, isPasswordValid, isPasswordCheckValid;
        TextInputLayout nameError,surnameError, emailError, passError, passErrorCheck;
        String patronPsw = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup);

            nombre = findViewById(R.id.nombre);
            apellidos = findViewById(R.id.apellidos);
            email = findViewById(R.id.email_header);
            psw = findViewById(R.id.password_login);
            pswCheck = findViewById(R.id.password_login_second);
            login = findViewById(R.id.login);
            signup = findViewById(R.id.signup);
            nameError = findViewById(R.id.nameError);
            surnameError = findViewById(R.id.surnameError);
            emailError = findViewById(R.id.emailError);
            passError = findViewById(R.id.passError);
            passErrorCheck = findViewById(R.id.passErrorCheck);

            signup.setOnClickListener(v -> {
                if (SetValidation()) {
                    FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email.getText().toString(),
                        psw.getText().toString()).addOnCompleteListener(a -> {
                            if (a.isSuccessful()) {
                                User user = new User(email.getText().toString(), psw.getText().toString(),
                                        nombre.getText().toString(), apellidos.getText().toString());
                                user.setSelfID(a.getResult().getUser().getUid());
                                DatabaseAdapter.getInstance().saveUser(user);
                                startActivity(new Intent(this, LogInActivity.class));
                            } else showAlert();
                    });
                }
            });

        }

    /**
     * Metodo para validar los datos del usuario
     * @return True si pasa todos los filtros, False si no
     */
    public boolean SetValidation() {
        // Validamos el nombre
        if (nombre.getText().toString().isEmpty()) {
            nameError.setError(getResources().getString(R.string.error_campo_vacio));
            Toast.makeText(getApplicationContext(), nameError.getError(), Toast.LENGTH_SHORT).show();
            isNameValid = false;
        } else if (nombre.getText().toString().length() >= 20) {
            nameError.setError(getResources().getString(R.string.demasiados_caracteres));
            Toast.makeText(getApplicationContext(), nameError.getError(), Toast.LENGTH_SHORT).show();
            isNameValid = false;
        } else {
            isNameValid = true;
            nameError.setErrorEnabled(false);
        }

        // Validamos los apellidos
        if (apellidos.getText().toString().isEmpty()) {
            surnameError.setError(getResources().getString(R.string.error_campo_vacio));
            Toast.makeText(getApplicationContext(), surnameError.getError(), Toast.LENGTH_SHORT).show();
            isNameValid = false;
        } else if (nombre.getText().toString().length() >= 30) {
            surnameError.setError(getResources().getString(R.string.demasiados_caracteres));
            Toast.makeText(getApplicationContext(), surnameError.getError(), Toast.LENGTH_SHORT).show();
            isNameValid = false;
        } else {
            isNameValid = true;
            surnameError.setErrorEnabled(false);
        }

        // Check for a valid email address.
        if (email.getText().toString().isEmpty()) {
            emailError.setError(getResources().getString(R.string.error_campo_vacio));
            isEmailValid = false;
            Toast.makeText(getApplicationContext(), emailError.getError(), Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            emailError.setError(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;
            Toast.makeText(getApplicationContext(), emailError.getError(), Toast.LENGTH_SHORT).show();
        } else {
            isEmailValid = true;
            emailError.setErrorEnabled(false);
        }

        // Check for a valid password.
        if (psw.getText().toString().isEmpty()) {
            passError.setError(getResources().getString(R.string.error_campo_vacio));
            isPasswordValid = false;
            Toast.makeText(getApplicationContext(), passError.getError(), Toast.LENGTH_SHORT).show();
        } else if (!psw.getText().toString().matches(patronPsw)) {
            passError.setError(getResources().getString(R.string.error_invalid_pwd));
            isPasswordValid = false;
            Toast.makeText(getApplicationContext(), passError.getError(), Toast.LENGTH_SHORT).show();
        } else  {
            isPasswordValid = true;
            passError.setErrorEnabled(false);
            passErrorCheck.setErrorEnabled(false);
        }

        if (pswCheck.getText().toString().isEmpty()) {
            passErrorCheck.setError(getResources().getString(R.string.error_campo_vacio));
            isPasswordCheckValid = false;
            Toast.makeText(getApplicationContext(), passErrorCheck.getError(), Toast.LENGTH_SHORT).show();
        } else if (!pswCheck.getText().toString().matches(patronPsw)) {
            passErrorCheck.setError(getResources().getString(R.string.error_invalid_pwd));
            isPasswordCheckValid = false;
            Toast.makeText(getApplicationContext(), passErrorCheck.getError(), Toast.LENGTH_SHORT).show();
        } else if (!psw.getText().toString().equals(pswCheck.getText().toString())) {
            passErrorCheck.setError(getResources().getString(R.string.error_invalid_pwd_Signup));
            isPasswordCheckValid = false;
            Toast.makeText(getApplicationContext(), passErrorCheck.getError(), Toast.LENGTH_SHORT).show();
        } else  {
            isPasswordCheckValid = true;
            passError.setErrorEnabled(false);
            passErrorCheck.setErrorEnabled(false);
        }

        if (isNameValid && isEmailValid && isPasswordValid && isPasswordCheckValid) {
            Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;

    }

    /**
     * Metodo de alerta del Firebase
     */
    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Se ha producido un error creando al usuario\n Pruebe de nuevo en otro momento");
        builder.setPositiveButton("Aceptar", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Método para guardar la nota en caso de que el usuario presione el botón atrás del móvil
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            setResult(RESULT_CANCELED, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
