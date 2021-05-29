package com.example.lize.workers;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lize.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;




public class AjustesActivity extends Activity {

    public static final int PICK_IMAGE = 1;
    private ArrayList<String> info;
    private String name, surnames, email, psw, ambitos;
    private EditText editName, editSurnames, editPsw, editEmail;
    boolean isNameValid, areSurnamesValid, isEmailValid, isPasswordValid;
    TextInputLayout nameInput, apellidosInput, emailInput, pswInput;
    ImageButton profilePicture;
    StorageReference storageReference;

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
        profilePicture = (ImageButton) findViewById(R.id.foto_de_perfil);

        nameInput = findViewById(R.id.nameInput);
        apellidosInput = findViewById(R.id.apellidosInput);
        emailInput = findViewById(R.id.emailInput);
        pswInput = findViewById(R.id.pswInput);
        storageReference = FirebaseStorage.getInstance().getReference();

        MaterialToolbar topAppBar = findViewById(R.id.ambito_material_toolbar);
        topAppBar.setOnMenuItemClickListener(this::onMenuItemClick);

        Button guardarCambios = findViewById(R.id.guardar_cambios);
        guardarCambios.setOnClickListener(v -> {
            if (validarDatos()){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Bundle userChanges = new Bundle();
                userChanges.putString("name", editName.getText().toString());
                userChanges.putString("surnames", editSurnames.getText().toString());
                userChanges.putString("email", editEmail.getText().toString());
                userChanges.putString("psw", editPsw.getText().toString());
                intent.putExtras(userChanges);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        profilePicture.setOnClickListener(v -> {
            selectImage();
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

    //Método para el tratamiento de los permisos de la aplicación
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_IMAGE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Intent para seleccionar una imagen del dispositivo para ponerla de perfil
    private void selectImage() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Selecciona una imagen");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    // Tratamiento de los datos de Intents de imagenes y documentos.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        profilePicture.setImageURI(selectedImageUri);
                        uploadImageToFirebase(selectedImageUri);

                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void uploadImageToFirebase(Uri selectedImageUri) {
        StorageReference fileRef = storageReference.child("profile.png");
        fileRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> Toast.makeText(AjustesActivity.this, "Imagen actualizada.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AjustesActivity.this, "Fallo al actualizar la imagen.", Toast.LENGTH_SHORT).show());
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
