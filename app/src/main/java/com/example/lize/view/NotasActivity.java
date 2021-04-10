package com.example.lize.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.lize.R;

import java.io.InputStream;


public class NotasActivity extends AppCompatActivity {

    private EditText inputNoteTitulo, inputNoteTexto;
    private String defaultTitle;
    private FrameLayout layoutNota;
    private ImageView imagenNota;

    public static int countId = 0;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    public static final int REQUEST_CODE_SELECT_IMAGE = 2;
    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);
        /*inputNoteTitulo = findViewById(R.id.inputNoteTitulo);*/
        //layoutNota = (FrameLayout) findViewById(R.id.layoutNota);
        inputNoteTexto = findViewById(R.id.inputNota);



        ImageView imagenAtras = findViewById(R.id.imagenAtras);
        imagenAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*saveNota();*/
                onBackPressed();
            }
        });

        ImageView imageView = findViewById(R.id.insertImageBtn);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            NotasActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }else{
                    selectImage();
                }
            }
        });
        imagenNota = findViewById(R.id.imageNote);
        registerForContextMenu(imagenNota);
        registerForContextMenu(inputNoteTexto);



    }

    private void selectImage() {


        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Selecciona una imagen");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);


                        imagenNota.setImageBitmap(bitmap);
                        imagenNota.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }

            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.inputNota){
            menu.add(0, v.getId(), 0, "Negrita");
            menu.add(0, v.getId(), 1, "Subrayado");
            menu.add(0, v.getId(), 2, "Subtitulo");
        }else if(v.getId() == R.id.imageNote){
            menu.add(0, v.getId(), 0, "Eliminar");
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("Eliminar")){
            imagenNota.setImageDrawable(null);
        }
        return true;
    }



    /*
    private void addEditText(){
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.text_note_layout, null);
        FrameLayout container = (FrameLayout) findViewById(R.id.layoutNota);
        container.addView(view);
    }*/

   /* private void addImageView(Bitmap bitmap){

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.image_note_layout, null);
        FrameLayout container = (FrameLayout) findViewById(R.id.layoutNota);
        container.addView(view);
        ImageView imageView = findViewById(R.id.imageNote);
        imageView.setImageBitmap(bitmap);


    }
*/


}