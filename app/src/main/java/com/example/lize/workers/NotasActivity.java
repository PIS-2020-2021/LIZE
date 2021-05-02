package com.example.lize.workers;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.adapters.DocumentAdapter;
import com.example.lize.data.Documento;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.RTToolbar;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;

import com.onegravity.rteditor.api.format.RTFormat;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;


import java.io.InputStream;
import java.util.ArrayList;


public class NotasActivity extends AppCompatActivity implements  BitmapGeneratingAsyncTask.Callback ,DocumentAdapter.OnDocumentListener {

    private static String DEFAULT_TITLE = "Titulo";
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    private static final int REQUEST_CODE_EDIT_NOTE = 2;

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
    private  RTApi rtApi;
    private  RTManager rtManager;
    private ViewGroup toolbarContainer;
    private  RTToolbar rtToolbar;
    private  RTEditText rtEditText;
    private FloatingActionButton KeyboardButton;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.RTE_ThemeLight);
        setContentView(R.layout.activity_notas);



        // Componentes
        inputNoteTitulo = findViewById(R.id.inputNoteTitulo);
        inputNoteTexto = findViewById(R.id.inputNota);
        carouselView = findViewById(R.id.carouselView);
        //imageLayout = findViewById(R.id.imageNote);
        ImageView backBtn = findViewById(R.id.backBtn);

        /**
        ImageView imageView = findViewById(R.id.insertImageBtn);
        ImageView documentBtn = findViewById(R.id.documentBtn);
**/

        // Apartado de documentos
        //ArrayList de imagenes y documentes
        images = new ArrayList<>();
        documents = new ArrayList<>();

        //Onclick Listener botones

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*saveNota();*/
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Bundle nota = new Bundle();
                nota.putString("title", inputNoteTitulo.getText().toString());
                nota.putString("noteText_HTML", rtEditText.getText(RTFormat.HTML));
                nota.putString("noteText_PLAIN", rtEditText.getText(RTFormat.PLAIN_TEXT));
                intent.putExtras(nota);
                setResult(validateNote(), intent);
                finish();
            }
        });





/*
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
        });*/

        /*documentBtn.setOnClickListener(new View.OnClickListener() {
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
*/
        //registerForContextMenu(inputNoteTexto);

        recyclerView = (RecyclerView) findViewById(R.id.fileAttachView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DocumentAdapter(documents,this);
        recyclerView.setAdapter(adapter);

        // create RTManager
          rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
          rtManager = new RTManager(rtApi, savedInstanceState);

// register toolbar
          toolbarContainer = (ViewGroup) findViewById(R.id.toolbar_container);
          rtToolbar = (RTToolbar) findViewById(R.id.rte_toolbar);


        if (rtToolbar != null) {
            rtManager.registerToolbar(toolbarContainer, rtToolbar);
        }

// register editor & set text
          rtEditText = (RTEditText) findViewById(R.id.inputNota);
        rtManager.registerEditor(rtEditText, true);
        //rtEditText.setRichTextEditing(true, message);



        getBundleForEdit();


    }

    private void getBundleForEdit() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String title = bundle.getString("title");
            String html_text = bundle.getString("noteText_HTML");
            Log.d("Titulo", title);
            Log.d("Texto HTML", html_text);

            inputNoteTitulo.setText(title);
            rtEditText.setRichTextEditing(true, html_text);
        }
    }

    private int validateNote() {
        if(inputNoteTitulo.getText().toString().isEmpty() && rtEditText.getText(RTFormat.PLAIN_TEXT).isEmpty())
            return RESULT_CANCELED;

        return RESULT_OK;
    }

    public void showMenu(View v) {
        //noinspection RestrictedApi
        MenuBuilder menuBuilder = new MenuBuilder(this);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.note_menu, menuBuilder);
        //noinspection RestrictedApi
        MenuPopupHelper optionsMenu = new MenuPopupHelper(this, menuBuilder, v);
        //noinspection RestrictedApi
        optionsMenu.setForceShowIcon(true);

        // Set Item Click Listener
        //noinspection RestrictedApi
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_document: // Handle option1 Click
                        selectDocument();
                        return true;


                    case R.id.toolbar_image:
                        selectImage();

                        return true;
                    case R.id.add_audio: // Handle option2 Click

                        return true;
                    case R.id.share:
                        updateBitmap();

                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {}
        });


        // Display the menu
        //noinspection RestrictedApi

        optionsMenu.show();


    }



    private void updateBitmap() {
        getEnteredWidthOrDefault();
        new BitmapGeneratingAsyncTask(this,rtEditText.getText(RTFormat.HTML), getEnteredWidthOrDefault(), this).execute();
    }

    @Override
    public void done(Bitmap bitmap) {

        String path = MediaStore.Images.Media.insertImage(this.getApplicationContext().getContentResolver(), bitmap,"test", null);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        shareIntent.setType("image/jpeg");

        startActivity(Intent.createChooser(shareIntent,"Share with" ));

    }

    public int getEnteredWidthOrDefault() {
        String enteredValue = rtEditText.getText(RTFormat.PLAIN_TEXT);



        if (!TextUtils.isEmpty(enteredValue)) {
           return rtEditText.getWidth();
        } else {
            return 150;
        }
    }




/*




/*
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_document:
                selectDocument();
                return true;
            case R.id.add_audio:

                return true;
            case R.id.add_reminder:

                return true;
            case R.id.share:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        rtManager.onSaveInstanceState(outState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        rtManager.onDestroy(isFinishing());

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
        Intent pickIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pickIntent.setType("*/*");
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent chooserIntent = Intent.createChooser(getIntent, "Selecciona un documento");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, REQUEST_DOCUMENT_GET);
    }




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
        menu.add(2, v.getId(), 0, "Eliminar");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Eliminar")) {
            images.remove(carouselView.getCurrentItem());
            init_carousel();
        }else if(item.getTitle().equals("Eliminar documento")){
            adapter.removeDocument(item.getGroupId());
            if(adapter.getItemCount() == 0 ){
                recyclerView.setVisibility(View.GONE);
            }
        }/*else if (item.getTitle().equals("Negrita")) {
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

        }*/
        return true;
    }

    @Override
    public void onDocumentClick(int position) {

        /*//File path = new File(getFilesDir(), "dl");
        //File file = new File(  documents.get(position).getAbsolutePath());
        File imagePath = new File(Context.getFilesDir(), "images");
        File newFile = new File(imagePath, "default_image.jpg");
        Uri contentUri = getUriForFile(this.getApplicationContext(), "com.mydomain.fileprovider", newFile);
        // Get URI and MIME type of file

        String mime = getContentResolver().getType(contentUri);

        // Open file with user selected app
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(contentUri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);*/
        }

}

