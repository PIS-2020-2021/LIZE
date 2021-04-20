package com.example.lize.workers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.adapters.DocumentAdapter;
import com.example.lize.data.Documento;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.w3c.dom.Text;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;


public class NotasActivity extends AppCompatActivity {

    private EditText inputNoteTitulo, inputNoteTexto;
    private CarouselView carouselView;
    private ArrayList<Bitmap> images;
    private ArrayList<Documento> documents;
    private RelativeLayout imageLayout;

    public static final int PICK_IMAGE = 1;
    public static final int REQUEST_DOCUMENT_GET = 2;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private DocumentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);

        // Componentes
        inputNoteTexto = findViewById(R.id.inputNota);
        carouselView = findViewById(R.id.carouselView);
        imageLayout = findViewById(R.id.imageNote);
        ImageView backBtn = findViewById(R.id.backBtn);
        ImageView imageView = findViewById(R.id.insertImageBtn);
        ImageView documentBtn = findViewById(R.id.documentBtn);


        // Apartado de documentos
        //ArrayList de imagenes y documentes
        images = new ArrayList<>();
        documents = new ArrayList<>();

        //Onclick Listener botones

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*saveNota();*/
                onBackPressed();
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            NotasActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PICK_IMAGE
                    );
                } else {
                    selectImage();
                }
            }
        });

        documentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            NotasActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_DOCUMENT_GET
                    );
                } else {
                    selectDocument();
                }
            }
            }
        );

        registerForContextMenu(inputNoteTexto);

        recyclerView = (RecyclerView) findViewById(R.id.fileAttachView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DocumentAdapter(documents);
        recyclerView.setAdapter(adapter);


    }


    // Carousel de imagenes de las notas
    private void init_carousel() {

        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageBitmap(images.get(position));
                registerForContextMenu(imageView);
            }
        };

        carouselView.setPageCount(images.size());
        carouselView.setImageListener(imageListener);
        if(images.isEmpty()){
            carouselView.setVisibility(View.GONE);
        }else{
            carouselView.setVisibility(View.VISIBLE);
        }

    }


    //Intent para seleccionar una imagen
    private void selectImage() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Selecciona una imagen");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    //Intent para seleccionar un documento

    private void selectDocument(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("*/*");
        pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent chooserIntent = Intent.createChooser(getIntent, "Selecciona un documento");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, REQUEST_DOCUMENT_GET);
    }



    // Gestión de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_IMAGE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }if (requestCode == REQUEST_DOCUMENT_GET && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectDocument();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Tratamiento de los datos de Intents de imagenes y documentos.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        images.add(0,bitmap);

                        init_carousel();
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }

            }
        }else if(requestCode == REQUEST_DOCUMENT_GET && resultCode == RESULT_OK){
            Uri uri = data.getData();
            String uriString = uri.toString();
            Documento myFile = new Documento(uriString);
            String path = myFile.getAbsolutePath();
            String displayName = null;

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }

            myFile.setName(displayName.substring(0,10));
            documents.add(myFile);
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
         }

    }

    // Context Menu para estilos de texto e imagenes
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.inputNota) {
            menu.add(0, v.getId(), 0, "Negrita");
            menu.add(0, v.getId(), 1, "Subrayado");
            menu.add(0, v.getId(), 2, "Subtitulo");
        }
        else{
            menu.add(2, v.getId(), 0, "Eliminar");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Eliminar")) {
            images.remove(carouselView.getCurrentItem());
            init_carousel();
        }else if(item.getTitle().equals("Eliminar documento")){
            adapter.removeDocument(item.getGroupId());
            if(adapter.getItemCount() == 0){
                recyclerView.setVisibility(View.GONE);
            }
        }else if (item.getTitle().equals("Negrita")) {
            SpannableStringBuilder stringBuilder = (SpannableStringBuilder) inputNoteTexto.getText();
            int selectionStart = inputNoteTexto.getSelectionStart();
            int selectionEnd = inputNoteTexto.getSelectionEnd();
            stringBuilder.setSpan(new StyleSpan(Typeface.BOLD), selectionStart, selectionEnd, 0);

        } else if (item.getTitle().equals("Subrayado")) {
            SpannableStringBuilder stringBuilder = (SpannableStringBuilder) inputNoteTexto.getText();
            int selectionStart = inputNoteTexto.getSelectionStart();
            int selectionEnd = inputNoteTexto.getSelectionEnd();
            stringBuilder.setSpan(new UnderlineSpan(), selectionStart, selectionEnd, 0);

        } else if (item.getTitle().equals("Subtitulo")) {
            SpannableStringBuilder stringBuilder = (SpannableStringBuilder) inputNoteTexto.getText();
            int selectionStart = inputNoteTexto.getSelectionStart();
            int selectionEnd = inputNoteTexto.getSelectionEnd();
            stringBuilder.setSpan(new RelativeSizeSpan(1.35f), selectionStart, selectionEnd, 0);
            stringBuilder.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), selectionStart, selectionEnd, 0);

        }
        return true;
    }

}

