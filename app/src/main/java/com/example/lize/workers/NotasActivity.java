package com.example.lize.workers;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
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
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.adapters.AudioAdapter;
import com.example.lize.adapters.DocumentAdapter;
import com.example.lize.data.Audio;
import com.example.lize.data.Document;
import com.example.lize.data.Image;
import com.example.lize.models.DocumentManager;
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

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



public class NotasActivity extends AppCompatActivity implements DocumentAdapter.OnDocumentListener, AudioAdapter.playerInterface {

    private static final String DEFAULT_TITLE = "Titulo";

    private EditText inputNoteTitulo;
    private CarouselView carouselView;
    public static final int PICK_IMAGE = 1;
    public static final int REQUEST_DOCUMENT_GET = 2;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 3;
    private RecyclerView documentRecycleView;
    private RecyclerView audioRecycleView;
    private LinearLayoutManager layoutManager;
    private DocumentAdapter documentAdapter;
    private AudioAdapter audioAdapter;
    private RTApi rtApi;
    private RTManager rtManager;
    private ViewGroup toolbarContainer;
    private RTToolbar rtToolbar;
    private RTEditText rtEditText;
    private CoordinatorLayout primaryLayout;
    private ConstraintLayout cLayout;
    private ScrollView scrollView;
    boolean isKeyboardShowing = false;
    private DocumentManager documentManager;
    private String documentsID;
    private String imagesID;
    private String audiosID;
    private MediaRecorder recorder;
    private boolean isRecording = false;
    private String fileName;
    private String singleAudioID;
    private long startAudio;
    private long endAudio;
    private Dialog recordDialog;
    private MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preferences.applySelectedTheme(this);
        setContentView(R.layout.activity_notas);
        setTheme(R.style.RTE_ThemeLight);

        // Componentes
        inputNoteTitulo = findViewById(R.id.inputNoteTitulo);
        carouselView = findViewById(R.id.carouselView);
        ImageView backBtn = findViewById(R.id.backBtn);
        primaryLayout = findViewById(R.id.coordinator);
        cLayout = findViewById(R.id.cLayout);
        scrollView = findViewById(R.id.scrollView);

        /* DOCUMENTOS / IMAGENES */
        //ArrayList de imagenes y documentes
        documentManager = DocumentManager.getInstance();

        //Onclick Listener botones
        backBtn.setOnClickListener(v -> saveNote());

        documentRecycleView = (RecyclerView) findViewById(R.id.fileAttachView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        documentRecycleView.setLayoutManager(layoutManager);
        documentAdapter = new DocumentAdapter(this);
        documentRecycleView.setAdapter(documentAdapter);

        /* AUDIOS  */
        audioRecycleView = (RecyclerView) findViewById(R.id.audioRecycleView);
        audioRecycleView.setLayoutManager(new LinearLayoutManager(this));
        audioAdapter = new AudioAdapter(this,this);
        audioRecycleView.setAdapter(audioAdapter);
        player = new MediaPlayer();
        recordDialog = new Dialog(this);

        // Crear RTManager para gestionar los botones de estilo
        rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
        rtManager = new RTManager(rtApi, savedInstanceState);

        // Asignamos el layout del toolbar de estilos
        toolbarContainer = (ViewGroup) findViewById(R.id.toolbar_container);
        rtToolbar = (RTToolbar) findViewById(R.id.rte_toolbar);
        if (rtToolbar != null) rtManager.registerToolbar(toolbarContainer, rtToolbar);

        // Register editor & set text
        rtEditText = (RTEditText) findViewById(R.id.inputNota);
        rtManager.registerEditor(rtEditText, true);
        getBundleForEdit();


        // ContentView is the root view of the layout of this activity/fragment
        primaryLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
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
            });
    }

    /**
     * Metodo para cambiar el aspecto del teclado al abrirlo o cerrarlo
     * @param opened True si está abierto, False si lo cerramos
     */
    void onKeyboardVisibilityChanged(boolean opened) {
        if (opened) {
            rtEditText.setMaxHeight(1000);
            toolbarContainer.setVisibility(View.VISIBLE);
            carouselView.setVisibility(View.GONE);
            documentRecycleView.setVisibility(View.GONE);
        } else {
            if (rtEditText.getLineCount() <= 2) rtEditText.setMaxHeight(200);
            else rtEditText.setMaxHeight(primaryLayout.getHeight());

            toolbarContainer.setVisibility(View.GONE);

            /*&& !imagesUris.isEmpty()*/
            if (documentAdapter.getItemCount() != 0) {
                documentRecycleView.setVisibility(View.VISIBLE);
                //showHideFragment(carouselFragment);
            }
            if (!documentManager.arrayImagesEmpty(imagesID)) { carouselView.setVisibility(View.VISIBLE); }
        }

    }

    /**
     * Método para guardar la nota en caso de que el usuario presione el botón atrás del móvil
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            saveNote();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Método para recuperar el contenido de la nota al editar.
     */
    private void getBundleForEdit() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            documentsID = bundle.getString("documentsID");
            imagesID = bundle.getString("imagesID");
            audiosID = bundle.getString("audiosID");
            String title = bundle.getString("title");
            String html_text = bundle.getString("noteText_HTML");
            Log.d("Titulo", title);
            Log.d("Texto HTML", html_text);

            inputNoteTitulo.setText(title);
            rtEditText.setRichTextEditing(true, html_text);
            if (bundle.getBoolean("images"))  init_carousel();

            if (bundle.getBoolean("documents")) {
                for (Document doc : documentManager.getDocuments(documentsID))
                    documentAdapter.addDocument(doc);
                documentRecycleView.setVisibility(View.VISIBLE);
            }
            if (bundle.getBoolean("audios")) {
                for (Audio s: documentManager.getAudios(audiosID))
                    audioAdapter.addAudio(s);
                audioAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Función para validar el contenido de la nota antes de agregarla a base de datos
     * @return status code CANCELLED o OK
     */
    private int validateNote() {
        if (inputNoteTitulo.getText().toString().isEmpty() && rtEditText.getText(RTFormat.PLAIN_TEXT).isEmpty() && audioAdapter.getItemCount() == 0 && documentManager.arrayImagesEmpty(imagesID)  && documentAdapter.getItemCount() == 0  /*&&  imagesUris.isEmpty()*/)
            return RESULT_CANCELED;
        return RESULT_OK;
    }

    //Método que construye el menú de tres puntos.

    /**
     * Metodo para desplegar el menú de opciones para insertar en la nota (menú de 3 puntos)
     * @param v View a desplegar
     */
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
            public boolean onMenuItemSelected(@NotNull MenuBuilder menu, @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_document:
                        selectDocument();
                        return true;
                    case R.id.toolbar_image:
                        selectImage();
                        return true;
                    case R.id.add_audio:
                        add_audio();
                        return true;
                    case R.id.share:
                        shareNote();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onMenuModeChange(@NotNull MenuBuilder menu) {
            }
        });
        // Display the menu
        //noinspection RestrictedApi
        optionsMenu.show();
    }

    /**
     * Metodo para compartir una nota
     */
    private void shareNote() {
        Intent intent2 = new Intent();
        intent2.setAction(Intent.ACTION_SEND);
        intent2.setType("text/html");
        intent2.putExtra(Intent.EXTRA_TEXT, rtEditText.getText(RTFormat.PLAIN_TEXT));
        startActivity(Intent.createChooser(intent2, "Share via"));
    }

    /**
     * Método para crear el intent para regresar los datos al Main activity
     */
    private void saveNote() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Bundle nota = new Bundle();
        nota.putString("title", inputNoteTitulo.getText().toString());
        nota.putString("noteText_HTML", rtEditText.getText(RTFormat.HTML));
        nota.putString("noteText_PLAIN", rtEditText.getText(RTFormat.PLAIN_TEXT));
        nota.putBoolean("images", !documentManager.arrayImagesEmpty(imagesID));
        nota.putBoolean("documents", !(documentAdapter.getItemCount() == 0));
        nota.putBoolean("audios", !(audioAdapter.getItemCount() == 0));
        nota.putString("documentsID", documentsID);
        nota.putString("imagesID", imagesID);
        nota.putString("audiosID", audiosID);

        intent.putExtras(nota);
        setResult(validateNote(), intent);
        finish();
    }

    /**
     * Este método calcula el tamaño por defecto que tendrá el editText
     * @return Tamaño del edit text
     */
    public int getEnteredWidthOrDefault() {
        String enteredValue = rtEditText.getText(RTFormat.PLAIN_TEXT);
        if (!TextUtils.isEmpty(enteredValue)) return rtEditText.getWidth();
        else return 150;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        rtManager.onSaveInstanceState(outState);

    }

    /**
     * Cierre del manager de edición cuando la actividad finaliza
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        rtManager.onDestroy(isFinishing());

    }

    /**
     * Metodo para iniciar el Carousel de imagenes de las notas
     */
    private void init_carousel() {
        ImageListener imageListener = (position, imageView) -> {
            Bitmap bitmap = BitmapFactory.decodeFile(documentManager.selectImageFromArray(imagesID, position));
            imageView.setImageBitmap(bitmap);
            registerForContextMenu(imageView);
        };

        carouselView.setPageCount(documentManager.imagesArraySize(imagesID));
        carouselView.setImageListener(imageListener);

        if (documentManager.arrayImagesEmpty(imagesID)) carouselView.setVisibility(View.GONE);
        else carouselView.setVisibility(View.VISIBLE);
    }

    /**
     * Intent para seleccionar una imagen del dispositivo e insertarlo en el Carrusel
     */
    private void selectImage() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Selecciona una imagen");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    /**
     * Intent para seleccionar un documento y agregarlo al apartado documentos de la nota
     */
    private void selectDocument() {
        Intent pickIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pickIntent.setType("*/*");
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent chooserIntent = Intent.createChooser(getIntent, "Selecciona un documento");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        startActivityForResult(chooserIntent, REQUEST_DOCUMENT_GET);
    }

    /**
     * Método para el tratamiento de los permisos de la aplicación
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PICK_IMAGE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) selectImage();
            else Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_DOCUMENT_GET && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) selectDocument();
            else Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) add_audio();
            else Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Tratamiento de los datos de Intents de imagenes y documentos.
     */
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
                        imagesID = documentManager.addImageToCloud(imagesID, i);
                        init_carousel();
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else if (requestCode == REQUEST_DOCUMENT_GET && resultCode == RESULT_OK) {
            assert data != null;
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
            } else if (uriString.startsWith("file://")) displayName = myFile.getName();

            myFile.setName(displayName);
            myFile.setId(displayName);

            documentsID = documentManager.addDocumentToCloud(documentsID, myFile);
            documentAdapter.addDocument(myFile);
            documentRecycleView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Context Menu para estilos de texto e imagenes
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(2, v.getId(), 0, "Eliminar");

    }

    /**
     * Esta función realiza las opciones de eliminar documento e imagen en función del elemento seleccionado
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Eliminar")) {
            documentManager.removeImageFromNote(imagesID, carouselView.getCurrentItem());
            init_carousel();
        } else if (item.getTitle().equals("Eliminar documento")) {
            documentManager.removeDocumentFromNote(documentsID, documentAdapter.getDocument(item.getGroupId()));
            documentAdapter.removeDocument(item.getGroupId());
            if (documentAdapter.getItemCount() == 0) documentRecycleView.setVisibility(View.GONE);
        }
        return true;
    }

    //TODO funcionalidad de abrir documentos en aplicaciones externas

    /**
     * Metodo para abrir un Documento de una Nota en una app externa
     * @param position posicion del Documento
     */
    @Override
    public void onDocumentClick(int position)  {
        Document d = documentManager.getDocuments(documentsID).get(position);
        //openFile(Uri.fromFile(d.getContent()));
    }

    /**
     * Metodo para conseguir el MimeType de una Nota con 2 parametros
     * @param mContext Contexto d ela nota
     * @param uri Uri del documento
     * @return mimeType
     */
    public static String getMimeType(Context mContext, Uri uri) {
        ContentResolver cR = mContext.getContentResolver();
        String mimeType = cR.getType(uri);
        if (mimeType == null) mimeType = getMimeType(uri.toString());

        return mimeType;
    }

    /**
     * Metodo para conseguir el MimeType de una Nota con 2 parametros
     * @param url URL del documento
     * @return mimeType
     */
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    /**
     * Metodo para abrir un file
     * @param url URL del file
     */
    private void openFile(Uri url) {
        try {
            Log.d("NotasActivity", "openFile: " + url.toString());

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(url, "application/msword");
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(url, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(url, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(url, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip")) {
                // ZIP file
                intent.setDataAndType(url, "application/zip");
            } else if (url.toString().contains(".rar")) {
                // RAR file
                intent.setDataAndType(url, "application/x-rar-compressed");
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(url, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(url, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(url, "image/gif");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") ||
                       url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(url, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(url, "text/plain");
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
                       url.toString().contains(".mpeg")|| url.toString().contains(".mpe") ||
                       url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(url, "video/*");
            } else {
                intent.setDataAndType(url, "*/*");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Metodo para conseguir el contenido de un InputStrem
     * @param inputStream InputStreem entrante para leer
     * @return Array de Bytes con la informacion
     * @throws IOException Excepcion debida a un fallo de escritura en el Buffer
     */
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024, len;
        byte[] buffer = new byte[bufferSize];

        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    /**
     * Metodo par añadir un Audio a una Nota
     */
    private void add_audio() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            Button cancel;
            ImageButton record;
            recordDialog.setContentView(R.layout.record_pop_up);

            cancel = (Button) recordDialog.findViewById(R.id.cancel_button);
            record = (ImageButton) recordDialog.findViewById(R.id.record);

            cancel.setOnClickListener(v -> {
                if (isRecording) {
                    isRecording = false;
                    stopRecording();
                }
                recordDialog.dismiss();
            });

            record.setOnClickListener(v -> {
                if (isRecording) {
                    stopRecording();
                    recordDialog.dismiss();
                } else {
                    record.setImageResource(R.drawable.ic_baseline_stop_24);
                    startRecording();
                }
            });
            recordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            recordDialog.show();
        }
    }

    /**
     * Metodo para empezar a grabar un Audio
     */
    private void startRecording() {
        Log.d("startRecording", "startRecording");

        recorder = new MediaRecorder();
        DateFormat df = new SimpleDateFormat("yyMMddHHmmss", Locale.ITALY);
        String date = df.format(Calendar.getInstance().getTime());
        fileName = getExternalCacheDir().getAbsolutePath() + File.separator + date + ".3gp";
        singleAudioID = date;
        Log.d("startRecording", fileName);

        recorder.setOutputFile(fileName);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.d("startRecording", "prepare() failed");
        }
        recorder.start();
        startAudio = System.currentTimeMillis();
        isRecording = true;
    }

    /**
     * Metodo para dejar de grabar un Audio
     */
    private void stopRecording() {
        if (isRecording) {
            endAudio = System.currentTimeMillis();
            recorder.stop();
            recorder.release();
            Audio a = new Audio(singleAudioID,fileName,(endAudio-startAudio));
            audiosID = documentManager.addAudioToCloud(audiosID,a);
            audioAdapter.addAudio(a);
            recorder = null;
            isRecording = false;
        }
    }

    /**
     * Metodo para empezar a reproducir un Audio
     * @param position Posicion desde la que se empezará a reproducir
     */
    public void startPlaying(int position) {
        try {
            if (player != null) {
                if (!player.isPlaying()) {
                    player = new MediaPlayer();
                    String fileName = audioAdapter.getAudio(position).getAddress();
                    Log.d("startPlaying", fileName);
                    player.setDataSource(fileName);
                    player.prepare();
                }
                audioAdapter.setSessionID(player.getAudioSessionId());
                player.start();
                player.setOnCompletionListener(mp -> {
                    audioAdapter.setSessionID(-1);
                    audioAdapter.setStateReproduction(position);
                    audioAdapter.notifyItemChanged(position);
                    Toast.makeText(getApplicationContext(), "Audio finished!", Toast.LENGTH_SHORT).show();
                    //playButton.setBackgroundResource(R.drawable.ic_baseline_play_circle_filled_24);
                });
            }
        } catch (IOException e) {
            Log.d("startPlaying", "prepare() failed");
        }
    }

    /**
     * Metodo para pausar la reproduccion de un Audio
     * @param position Posicion del Audio
     */
    @Override
    public void pausePlaying(int position) {
        try {
            player.pause();
        } catch (IllegalStateException e) {
            Toast.makeText(this,"No se está reporduciendo ningún audio",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Metodo para eliminar un Audio de una Nota
     * @param position posicion del Audio
     */
    @Override
    public void removeAudio(int position) {
        documentManager.removeAudioFromNote(audiosID,audioAdapter.getAudio(position));
    }
}

