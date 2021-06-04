package com.example.lize.workers;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lize.R;
import com.example.lize.models.DocumentManager;
import com.example.lize.models.MainViewModel;
import com.example.lize.utils.Preferences;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

/** Activity Principal de la app Lize. Contenedor del Ámbito con sus Carpetas y sus Notas. */
public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    public static final String TAG = "MainActivity";
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    private static final int REQUEST_CODE_EDIT_NOTE = 2;
    private static final int REQUEST_CODE_ADD_AMBITO = 3;
    public static final int REQUEST_CODE_EDIT_AMBITO = 4;
    private static final int REQUEST_CODE_UPDATE_USER = 5;
    private static final int THEME_UPDATE_DURATION = 1000;

    private MaterialToolbar topAppBar;                       // MaterialToolbar de la app.
    private NoteHostFragment noteHostFragment;               // Contenedor de Notas
    private FolderHostFragment folderHostFragment;           // Contenedor de Folders
    private AmbitoHostFragment ambitoHostFragment;           // Contenedor de Ambitos
    private boolean cardNoteType = true;                     // Boolean del tipo de vista de las Notas.

    private MainViewModel dataViewModel;
    private DocumentManager documentManager;

    private FloatingActionButton addFAB, addNoteFAB, addFolderFAB;  // Floating Action Buttons
    private boolean isFABGroupExpanded = false;
    private final Handler handler = new Handler();        // Methods to create a simple animation
    private final Timer t = new Timer();
    private DrawerLayout drawerLayout;              //Declaramos DrawerLayout
    private ActionBarDrawerToggle mDrawerToggle;    //Declaramos Toggle
    private Button addAmbito;                       //Declaramos Botones
    private Button darkMode;
    private Button signOut;
    private Button settings;

    private Toast toastReference;

    /** Main constructor */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Preferences.applySelectedTheme(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawable_nav);

        /*Asignamos el objeto toolBar de la view*/
        this.topAppBar = findViewById(R.id.ambito_material_toolbar);

        //Asignamos el Drawer Object a la view
        drawerLayout = findViewById(R.id.drawerLayout);
        //Creamos el DrawerToggle Object
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, topAppBar, R.string.openDrawer, R.string.closeDrawer){
            @Override
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                //Si quisieramos que pasase algo cuando abramos el drawer, esta es la función
            }

            @Override
            public void onDrawerClosed(View drawerView){
                super.onDrawerClosed(drawerView);
                //Si quisieramos que pasase algo cuando cerremos el drawer, esta es la función
                dataViewModel.savePositionAmbitos();
            }
        };


        //Asignamos el Drawer Listener a Drawer Toggle
        drawerLayout.setDrawerListener(mDrawerToggle);
        //Configuramos el DrawerToggle par que sincronice con el Estado
        mDrawerToggle.syncState();

        //Asignamos Boton y Listener a addAmbito
        this.addAmbito = findViewById(R.id.addAmbitoButton);
        addAmbito.setOnClickListener(v -> {
            if (dataViewModel.getUserSelected().getValue().getAmbitos().size() == 9) {
                toastReference = Toast.makeText(getBaseContext(), "Has alcanzado el número máximo de ámbitos.", Toast.LENGTH_SHORT);
                toastReference.show();
                toastReference = Toast.makeText(getBaseContext(), "Elimina algún ámbito, por favor.", Toast.LENGTH_SHORT);
                toastReference.show();
            } else {
                Intent intent = new Intent(getApplicationContext(), NewAmbitoActivity.class);
                intent.putIntegerArrayListExtra("Ambitos", dataViewModel.getUserSelected().getValue().getColorsTaken());
                startActivityForResult(intent, REQUEST_CODE_ADD_AMBITO);
            }
        });

        //Asignamos Boton y Listener a addAmbito
        this.darkMode = findViewById(R.id.darkModeButton);
        darkMode.setOnClickListener(v -> {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                toastReference = Toast.makeText(getBaseContext(), "Cambiando a Modo Oscuro.", Toast.LENGTH_SHORT);
                toastReference.show();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                toastReference = Toast.makeText(getBaseContext(), "Cambiando a Modo Claro.", Toast.LENGTH_SHORT);
                toastReference.show();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });

        //Asignamos Boton y Listener a settings
        this.settings = findViewById(R.id.settingsButton);
        settings.setOnClickListener(v -> {
            Intent intent_Settings = new Intent(this, AjustesActivity.class);
            intent_Settings.putStringArrayListExtra("Info_User", dataViewModel.getUserSelected().getValue().getInfoUser());
            startActivityForResult(intent_Settings, REQUEST_CODE_UPDATE_USER);
        });

        //Asignamos Boton y Listener a signOut
        this.signOut = findViewById(R.id.sign_out);
        signOut.setOnClickListener(v -> {
            dataViewModel.savePositionAmbitos();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            startActivity(intent);
        });

        // Obtenemos los componentes y registramos Listeners
        this.ambitoHostFragment = (AmbitoHostFragment) getSupportFragmentManager().findFragmentById(R.id.ambitos_host_fragment);
        this.folderHostFragment = (FolderHostFragment) getSupportFragmentManager().findFragmentById(R.id.folders_host_fragment);
        this.noteHostFragment = (NoteHostFragment) getSupportFragmentManager().findFragmentById(R.id.notes_host_fragment);
        topAppBar.setOnMenuItemClickListener(this);

        // Inicializamos los FABS
        initFABGroup();

        //Observamos el LiveData del ViewModel
        observeLiveData();

        documentManager = documentManager.getInstance();
        documentManager.setContext(this);
     }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * Inicializamos el ViewModel del MainActivity y seteamos los observadores de los LivesData.
     */
    private void observeLiveData() {
        this.dataViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Observador del Toast Message del MainActivity. Evitamos acumulación de toasts mediante toastReference.
        dataViewModel.getToast().observe(this, (t) -> {
            if (dataViewModel.getViewUpdate().getValue()) {
                if (toastReference != null) toastReference.cancel();
                toastReference = Toast.makeText(this.getBaseContext(), t, Toast.LENGTH_SHORT);
                toastReference.show();
            }
        });

        // Observador del Usuario seleccionado. Inicializa el HeaderNavigationView.
        dataViewModel.getUserSelected().observe(this, user -> initHeaderNavigationView(user.getFirst() + " " + user.getLast(), user.getMail(), user.getSelfID()));

        // Observador del Ámbito seleccionado.
        dataViewModel.getAmbitoSelected().observe(this, (ambito) -> {
            drawerLayout.closeDrawer(Gravity.LEFT);
            if (dataViewModel.getViewUpdate().getValue()) topAppBar.setTitle(ambito.getName());
            else updateAmbito();
        });
    }

    /**
     * Implementación del método OnMenuItemClick para definir las acciones de los items del Toolbar.
     * @param item item del Toolbar: search, sandwich
     * @return True si se ha hecho click en alguno de ellos, False si no
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                noteHostFragment.searchNote(item);
                break;
            case R.id.sandwich:
                changeCardNoteType(item);
                noteHostFragment.changeCardNoteType();
                break;
            default: return false;
        }
        return true;
    }

    /**
     * Método para modificar el icono del MenuItem de cambiar vista de Notas
     * @param item menú item de cambiar vista de notas (sandwich)
     */
    private void changeCardNoteType(MenuItem item) {
        cardNoteType = !cardNoteType;
        item.setIcon((cardNoteType) ? R.drawable.ic_baseline_table_rows_24 : R.drawable.ic_baseline_view_module_24);
    }

    /**
     * Retornamos de la actividad NotasActivity y guardamos los cambios realizados
     * @param requestCode Petición de codigo
     * @param resultCode Resultado del Code
     * @param data Intent de retorno
     */
    // TODO: Ojo que peta al acceder a un documento! data.getExtras con data null.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            Log.d(TAG, "Received new Bundle Data: " + bundle.toString());
            if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
                String title = bundle.getString("title");
                String plainText = bundle.getString("noteText_PLAIN");
                String htmlText = bundle.getString("noteText_HTML");
                Log.d("Titulo", title);
                Log.d("Texto Plano", plainText);
                Log.d("Texto HTML", htmlText);

                Boolean images = bundle.getBoolean("images");
                Boolean documents = bundle.getBoolean("documents");
                Boolean audios = bundle.getBoolean("audios");
                String  documentsID =  bundle.getString("documentsID");
                String  imagesID =  bundle.getString("imagesID");
                String  audiosID = bundle.getString("audiosID");

                dataViewModel.addNote(title, plainText, htmlText, images, documents,audios,documentsID,imagesID,audiosID);

            } else if (requestCode == REQUEST_CODE_EDIT_NOTE && resultCode == RESULT_OK) {
                Log.d(TAG, "Request Code for Note Editing OK");
                String title = bundle.getString("title");
                String plainText = bundle.getString("noteText_PLAIN");
                String htmlText = bundle.getString("noteText_HTML");
                Boolean images = bundle.getBoolean("images");
                String imagesID =  bundle.getString("imagesID");
                Boolean documents = bundle.getBoolean("documents");
                String documentsID =  bundle.getString("documentsID");
                Boolean audios = bundle.getBoolean("audios");
                String  audiosID = bundle.getString("audiosID");
                dataViewModel.editNote(title, plainText, htmlText, images, documents,audios,documentsID,imagesID,audiosID);

            } else if (requestCode == REQUEST_CODE_ADD_AMBITO && resultCode == RESULT_OK) {
                String name = bundle.getString("name");
                int color = (int) bundle.getLong("color");
                Log.d("Nombre", name);
                Log.d("Color ", String.valueOf(color));

                dataViewModel.addAmbito(name, color);
                // ambitoHostFragment.addAmbito(name, color);

            } else if(requestCode == REQUEST_CODE_EDIT_AMBITO && resultCode == RESULT_OK) {
                String name = bundle.getString("name");
                int color = (int) bundle.getLong("color");
                String selfID = bundle.getString("selfID");
                Log.d("Nombre", name);
                Log.d("Color ", String.valueOf(color));
                Log.d("SelfID ", String.valueOf(selfID));

                dataViewModel.editAmbito(selfID, name, color);

            } else if (requestCode == REQUEST_CODE_UPDATE_USER && resultCode == RESULT_OK) {
                String name = bundle.getString("name");
                String surnames = bundle.getString("surnames");
                String email = bundle.getString("email");
                String psw = bundle.getString("psw");
                Log.d("Nombre", name);
                Log.d("Apellidos", surnames);
                Log.d("Email", email);
                Log.d("Contraseña", psw);
                dataViewModel.editUser(name, surnames, email, psw);
                dataViewModel.getUserSelected().observe(this, user -> initHeaderNavigationView(user.getFirst() + " " + user.getLast(), user.getMail(), user.getSelfID()));

            } else Log.d(TAG, "Invalid RESULT from NoteActivity: " + resultCode);
        }
        else Log.d(TAG, "Null bundle");

    }
    /**
     * Setea el Header de la MainActivity
     * @param name nombre del User
     * @param eMail email del User
     */
    private void initHeaderNavigationView(String name, String eMail, String userID) {
        View header = findViewById(R.id.headerView);

        TextView headerName = header.findViewById(R.id.name_header);
        TextView headerEMail = header.findViewById(R.id.email_header);
        CircleImageView headerImgProfile = header.findViewById(R.id.circleImageView_header);

        if (name != null) headerName.setText(name);
        if (eMail != null)  headerEMail.setText(eMail);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("/profileUser_" + userID + ".png");
        profileRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> Picasso.get().load(downloadUrl).into(headerImgProfile));

    }

    /**
     * Inicializamos los fabs de creación de notas y carpetas
     */
    private void initFABGroup() {
        this.addFAB = findViewById(R.id.add_button);
        this.addNoteFAB = findViewById(R.id.add_note_button);
        this.addFolderFAB = findViewById(R.id.add_folder_button);
        addFAB.setOnClickListener((v)-> {
            if (!isFABGroupExpanded) expandFABGroup();
            else closeFABGroup();
            isFABGroupExpanded = !isFABGroupExpanded;
        });
        addNoteFAB.setOnClickListener((v)-> startActivityForResult(new Intent(getApplicationContext(), NotasActivity.class), REQUEST_CODE_ADD_NOTE));
        addFolderFAB.setOnClickListener(this::showFolderMenu);
    }

    /**
     * Lógica de expansión
     */
    private void expandFABGroup() {
        addFAB.animate().rotationBy(- getResources().getInteger(R.integer.fab_rotation));
        addNoteFAB.animate().translationY(- getResources().getDimension(R.dimen.fab_translation_1));
        addFolderFAB.animate().translationY(- getResources().getDimension(R.dimen.fab_translation_2));
        t.schedule(new TimerTask() {
            @Override
            public void run() { handler.post(()-> {
                addNoteFAB.setVisibility(View.VISIBLE);
                addFolderFAB.setVisibility(View.VISIBLE);
        }); }}, 120);
    }

    /**
     * Lógica de cierre
     */
    private void closeFABGroup() {
        addFAB.animate().rotation(0);
        addNoteFAB.animate().translationY(0);
        addFolderFAB.animate().translationY(0);
        t.schedule(new TimerTask() {
            @Override
            public void run() { handler.post(()-> {
                addNoteFAB.setVisibility(View.INVISIBLE);
                addFolderFAB.setVisibility(View.INVISIBLE);
        });}}, 200);
    }

    /**
     * Menu desplegable para la creación de una nueva carpeta
     * @param v fab de nueva carpeta
     */
    private void showFolderMenu(View v) {
        View popupView = getLayoutInflater().inflate(R.layout.popup_add, null);
        PopupWindow popupWindow = new PopupWindow(popupView, 800, 600);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        TextInputLayout saveDescr = popupView.findViewById(R.id.note_description);
        Button saveButton = popupView.findViewById(R.id.save_button);
        saveButton.setOnClickListener((w)->{
            String folderName = saveDescr.getEditText().getText().toString();
            folderHostFragment.addFolderChip(folderName);
            popupWindow.dismiss();
        });
    }

    /**
     * Lógica de actualización de Vista. Realizamos una pequeña animación antes de recrear la actividad.
     */
    protected void updateAmbito() {
        ViewGroup root = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        CircularProgressIndicator circleProgress = findViewById(R.id.progress_circle);
        Animation recreate = new Animation(){};
        recreate.setDuration(THEME_UPDATE_DURATION);
        recreate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (toastReference != null) toastReference.cancel();
                toastReference = Toast.makeText(getBaseContext(), "Cargando Ámbito...", Toast.LENGTH_SHORT);
                toastReference.show();
                circleProgress.show();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dataViewModel.getViewUpdate().setValue(true);
                circleProgress.hide();
                recreate();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        root.startAnimation(recreate);
    }
    /**
     * Método para guardar la nota en caso de que el usuario presione el botón atrás del móvil
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            toastReference = Toast.makeText(getBaseContext(), "Estas en la pantalla principal.", Toast.LENGTH_SHORT);
            toastReference.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}