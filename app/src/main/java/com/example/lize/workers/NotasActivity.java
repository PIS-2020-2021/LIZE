package com.example.lize.workers;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.MimeTypeMap;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.adapters.DocumentAdapter;
import com.example.lize.data.Document;
import com.example.lize.data.Image;
import com.example.lize.models.DocumentManager;
import com.example.lize.utils.FileUtils;
import com.example.lize.utils.Preferences;
import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.RTToolbar;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;

import com.onegravity.rteditor.api.format.RTFormat;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class NotasActivity extends AppCompatActivity implements DocumentAdapter.OnDocumentListener {

    private static final String DEFAULT_TITLE = "Titulo";

    private EditText inputNoteTitulo, inputNoteTexto;
    private CarouselView carouselView;
    private FileUtils fileUtils;
    //private ArrayList<Document> documents;
    public static final int PICK_IMAGE = 1;
    public static final int REQUEST_DOCUMENT_GET = 2;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private DocumentAdapter adapter;
    private RTApi rtApi;
    private RTManager rtManager;
    private ViewGroup toolbarContainer;
    private RTToolbar rtToolbar;
    private RTEditText rtEditText;
    private CoordinatorLayout primaryLayout;
    private ConstraintLayout cLayout;
    private ScrollView scrollView;
    private int NoteHeight;
    boolean isKeyboardShowing = false;
    private View root;
    private DocumentManager documentManager;
    private String documentsID;
    private String imagesID;
    private boolean initRecycleView = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.RTE_ThemeLight);
        Preferences.applySelectedTheme(this);
        setContentView(R.layout.activity_notas);
        setTheme(R.style.RTE_ThemeLight);

        // Componentes
        inputNoteTitulo = findViewById(R.id.inputNoteTitulo);
        inputNoteTexto = findViewById(R.id.inputNota);
        carouselView = findViewById(R.id.carouselView);
        ImageView backBtn = findViewById(R.id.backBtn);
        primaryLayout = findViewById(R.id.coordinator);
        cLayout = findViewById(R.id.cLayout);
        scrollView = findViewById(R.id.scrollView);

        // Apartado de documentos
        //ArrayList de imagenes y documentes
        fileUtils = new FileUtils(this);
        documentManager = documentManager.getInstance();
        //images = new ArrayList<>();
        //documents = new ArrayList<>();
        //Onclick Listener botones
        backBtn.setOnClickListener(v -> saveNote());
        recyclerView =  (RecyclerView) findViewById(R.id.fileAttachView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DocumentAdapter(this);
        recyclerView.setAdapter(adapter);

        // Crear RTManager para gestionar los botones de estilo
        rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
        rtManager = new RTManager(rtApi, savedInstanceState);

        // Asignamos el layout del toolbar de estilos
        toolbarContainer = (ViewGroup) findViewById(R.id.toolbar_container);
        rtToolbar = (RTToolbar) findViewById(R.id.rte_toolbar);


        if (rtToolbar != null) {
            rtManager.registerToolbar(toolbarContainer, rtToolbar);
        }

        // register editor & set text
        rtEditText = (RTEditText) findViewById(R.id.inputNota);
        rtManager.registerEditor(rtEditText, true);
        //rtEditText.setRichTextEditing(true, message);
        try {
            getBundleForEdit();
        } catch (IOException e) {
            e.printStackTrace();
        }

// ContentView is the root view of the layout of this activity/fragment
        primaryLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        primaryLayout.getWindowVisibleDisplayFrame(r);
                        int screenHeight = primaryLayout.getRootView().getHeight();

                        // r.bottom is the position above soft keypad or device button.
                        // if keypad is shown, the r.bottom is smaller than that before.
                        int keypadHeight = screenHeight - r.bottom;

                        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                            // keyboard is opened
                            if (!isKeyboardShowing) {
                                isKeyboardShowing = true;
                                onKeyboardVisibilityChanged(true);
                            }
                        } else {
                            // keyboard is closed
                            if (isKeyboardShowing) {
                                isKeyboardShowing = false;
                                onKeyboardVisibilityChanged(false);
                            }
                        }
                    }
                });


    }

    void onKeyboardVisibilityChanged(boolean opened) {


        if (opened) {
            rtEditText.setMaxHeight(1000);
            toolbarContainer.setVisibility(View.VISIBLE);
            carouselView.setVisibility(View.GONE);
            //rtEditText.setHeight(1000);
            recyclerView.setVisibility(View.GONE);
        } else {
            if(rtEditText.getLineCount() <=2 ){
                rtEditText.setMaxHeight(200);
            }else{
                rtEditText.setMaxHeight(primaryLayout.getHeight());
            }
            toolbarContainer.setVisibility(View.GONE);
            //rtEditText.setHeight(rtEditText.getLineCount() * 100);
            if (adapter.getItemCount() !=0/*&& !imagesUris.isEmpty()*/) {
                recyclerView.setVisibility(View.VISIBLE);
                 //showHideFragment(carouselFragment);
            }
            if(!documentManager.arrayImagesEmpty(imagesID)){
                carouselView.setVisibility(View.VISIBLE);
            }
        }

    }

    //Método para guardar la nota en caso de que el usuario presione el botón atrás del móvil
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            saveNote();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // Método para recuperar el contenido de la nota al editar.
    private void getBundleForEdit() throws IOException {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            documentsID = bundle.getString("documentsID");
            imagesID = bundle.getString("imagesID");
            String title = bundle.getString("title");
            String html_text = bundle.getString("noteText_HTML");
            Log.d("Titulo", title);
            Log.d("Texto HTML", html_text);

            inputNoteTitulo.setText(title);
            rtEditText.setRichTextEditing(true, html_text);
            if(bundle.getBoolean("images")){
                //images = documentManager.getImagesNote(documentsID);

                    init_carousel();
            }
            if(bundle.getBoolean("documents")){

                for(Document doc:  documentManager.getDocuments(documentsID)){
                    adapter.addDocument(doc);
                }
                recyclerView.setVisibility(View.VISIBLE);
            }

        }
    }

    //Función para validar el contenido de la nota antes de agregarla a base de datos
    private int validateNote() {
        if (inputNoteTitulo.getText().toString().isEmpty() && rtEditText.getText(RTFormat.PLAIN_TEXT).isEmpty() /*&&  imagesUris.isEmpty()*/)
            return RESULT_CANCELED;
        return RESULT_OK;
    }

    //Método que construye el menú de tres puntos.
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
                        shareNote();
                        //updateBitmap();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {
            }
        });

        // Display the menu
        //noinspection RestrictedApi
        optionsMenu.show();

    }

    private void shareNote() {
        Intent intent2 = new Intent();
        intent2.setAction(Intent.ACTION_SEND);
        intent2.setType("text/html");
        intent2.putExtra(Intent.EXTRA_TEXT, rtEditText.getText(RTFormat.PLAIN_TEXT));
        startActivity(Intent.createChooser(intent2, "Share via"));
    }

    //Método para crear el intent para regresar los datos al Main activity
    private void saveNote() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Bundle nota = new Bundle();
        nota.putString("title", inputNoteTitulo.getText().toString());
        nota.putString("noteText_HTML", rtEditText.getText(RTFormat.HTML));
        nota.putString("noteText_PLAIN", rtEditText.getText(RTFormat.PLAIN_TEXT));
        nota.putBoolean("images",!documentManager.arrayImagesEmpty(imagesID));
        nota.putBoolean("documents",!(adapter.getItemCount() == 0));
        nota.putString("documentsID", documentsID);
        nota.putString("imagesID", imagesID);
        intent.putExtras(nota);
        setResult(validateNote(), intent);
        finish();
    }



    //Este método calcula el tamaño por defecto que tendrá el editText
    public int getEnteredWidthOrDefault() {
        String enteredValue = rtEditText.getText(RTFormat.PLAIN_TEXT);
        if (!TextUtils.isEmpty(enteredValue)) {
            return rtEditText.getWidth();
        } else {
            return 150;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        rtManager.onSaveInstanceState(outState);

    }

    //Cierre del manager de edición cuando la actividad finaliza
    @Override
    protected void onDestroy() {
        super.onDestroy();
        rtManager.onDestroy(isFinishing());

    }

    // Carousel de imagenes de las notas
    private void init_carousel() {

        ImageListener imageListener = (position, imageView) -> {
                Bitmap bitmap = BitmapFactory.decodeFile(documentManager.selectImageFromArray(imagesID,position));
                imageView.setImageBitmap(bitmap);

            registerForContextMenu(imageView);
        };

        carouselView.setPageCount(documentManager.imagesArraySize(imagesID));
        carouselView.setImageListener(imageListener);

        if (documentManager.arrayImagesEmpty(imagesID)) {
            carouselView.setVisibility(View.GONE);
        } else {
            carouselView.setVisibility(View.VISIBLE);
        }
    }




    //Intent para seleccionar una imagen del dispositivo e insertarlo en el Carrusel
    private void selectImage() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Selecciona una imagen");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    //Intent para seleccionar un documento y agregarlo al apartado documentos de la nota
    private void selectDocument() {
        Intent pickIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pickIntent.setType("*/*");
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent chooserIntent = Intent.createChooser(getIntent, "Selecciona un documento");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, REQUEST_DOCUMENT_GET);
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
        if (requestCode == REQUEST_DOCUMENT_GET && grantResults.length > 0) {
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
                        Image i = documentManager.BitmapToImage(bitmap);
                        //images.add(i);
                        imagesID = documentManager.addImageToCloud(imagesID,i);
                        init_carousel();
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else if (requestCode == REQUEST_DOCUMENT_GET && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String uriString = uri.toString();
            Document myFile = new Document(uri);


            //String path = myFile.getAbsolutePath();
            String displayName = null;

            if (uriString.startsWith("content://")) {
                try (Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }

            myFile.setName(displayName);
            myFile.setId(displayName);

            documentsID = documentManager.addDocumentToCloud(documentsID,  myFile);
/*            if(!initRecycleView){
                adapter = new DocumentAdapter(documentManager.getDocuments(documentsID), this);
                recyclerView.setAdapter(adapter);
                initRecycleView = true;
            }*/
            adapter.addDocument(myFile);
            //adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

    // Context Menu para estilos de texto e imagenes
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(2, v.getId(), 0, "Eliminar");

    }

    //Esta función realiza las opciones de eliminar documento e imagen en función del elemento seleccionado
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Eliminar")) {
            documentManager.removeImageFromNote(imagesID,carouselView.getCurrentItem());
            init_carousel();
        } else if (item.getTitle().equals("Eliminar documento")) {

            documentManager.removeDocumentFromNote(documentsID,adapter.getDocument(item.getGroupId()));
            adapter.removeDocument(item.getGroupId());
            if (adapter.getItemCount() == 0) {
                recyclerView.setVisibility(View.GONE);
            }
        }
        return true;
    }

    //TODO funcionalidad de abrir documentos en aplicaciones externas
    @Override
    public void onDocumentClick(int position) throws FileNotFoundException {
        Document d = documentManager.getDocuments(documentsID).get(position);
        openFile(d.getUrl());
    }

    public static String getMimeType(Context mContext, Uri uri) {
        ContentResolver cR = mContext.getContentResolver();
        String mimeType = cR.getType(uri);
        if (mimeType == null) {
            mimeType = getMimeType(uri.toString());
        }
        return mimeType;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }


    private void openFile(Uri url) {

        try {

            Uri uri =url;;
            Log.d("NotasActivity", "openFile: " + url.toString());

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip")) {
                // ZIP file
                intent.setDataAndType(uri, "application/zip");
            } else if (url.toString().contains(".rar")){
                // RAR file
                intent.setDataAndType(uri, "application/x-rar-compressed");
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
                    url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                intent.setDataAndType(uri, "*/*");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}

