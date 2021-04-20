package com.example.lize.workers;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lize.R;
import com.example.lize.adapters.AmbitosAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

;import de.hdodenhof.circleimageview.CircleImageView;

/** Activity Principal de la app Lize. Contenedor del Ámbito con sus Carpetas y sus Notas. */
public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener{

    /*Mock-Up de Adrián */
    //Declaramos Títulos para nuestro Navigation Drawer List View
    String[] AMBITOS = {"Home", "Eventos", "Trabajo", "Universidad", "Otra Más","Home", "Eventos", "Trabajo", "Universidad", "Otra Más","Home", "Eventos", "Trabajo", "Universidad", "Otra Más","Home", "Eventos", "Trabajo", "Universidad", "Otra Más"};

    //Creamos un recurso String para el nombre y el eMail para el HeaderView
    //También creamos un recurso para la imagen de perfil del HeaderView
    String NAME = "Maribel Gonzalez";
    String EMAIL = "mabel@gmail.com";
    int IMG_PROFILE = R.drawable.fondo_inicio_app;





    private MaterialToolbar topAppBar;                       // MaterialToolbar de la app.
    private NoteHostFragment noteHostFragment;               // Contenedor de Notas
    private FolderHostFragment folderHostFragment;           // Contenedor de Folders
    private FloatingActionButton addFAB;                     // Floating Action Button de añadir
    private boolean cardNoteType = true;                     // Boolean del tipo de vista de las Notas.
    public static final int REQUEST_CODE_ADD_NOTE = 1;


    //Declaramos RecyclerView
    RecyclerView mRecyclerView;
    //Declaramos un Adapter par el Recycler View
    RecyclerView.Adapter mAdapter;
    //Declaramos un LayoutManager como Linear Layout Manager
    RecyclerView.LayoutManager mLayoutManager;
    //Declaramos un DrawerLayout
    DrawerLayout drawerLayout;

    //Declaramos un Action Bar Drawer Toggle
    ActionBarDrawerToggle mDrawerToggle;

    //Declaramos Los Botones
    Button addAmbito;
    Button signOut;


    /** Main constructor */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable_nav);

        /*Configuramos el Header con la info proporcionada*/
        initHeaderNavigationView(NAME, EMAIL, IMG_PROFILE);

        /*Asignamos el objeto toolBar de la view
        y después configuramos la Action Bar a nuestro ToolBar
        toolbar = findViewById(R.id.ambito_material_toolbar);
        setSupportActionBar(toolbar);*/
        this.topAppBar = findViewById(R.id.ambito_material_toolbar);

        //Asignamos el RecyclerView Object al xmlView
        mRecyclerView = findViewById(R.id.recyclerView);
        //Hacemos saber al sistema que la lista de objetos es fija
        //mRecyclerView.setHasFixedSize(true);

        //Creamos el Adaptador de MyAdapter class
        //Le pasamos los ambitos, iconos (si hubiera), nombre/mail del header e imagen del header
        mAdapter = new AmbitosAdapter(AMBITOS, NAME, EMAIL, IMG_PROFILE);

        //Configuramos el adapter al RecyclerView
        mRecyclerView.setAdapter(mAdapter);

        //Creamos un LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        //Configuramos el LinearLayoutManager
        mRecyclerView.setLayoutManager(mLayoutManager);


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
            }
        };

        //Asignamos el Drawer Listener a Drawer Toggle
        drawerLayout.setDrawerListener(mDrawerToggle);
        //Configuramos el DrawerToggle par que sincronice con el Estado
        mDrawerToggle.syncState();

        this.addAmbito = findViewById(R.id.addAmbitoButton);
        addAmbito.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewAmbito.class);
                startActivity(intent);
            }
        });

        this.signOut = findViewById(R.id.sign_out);
        signOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(intent);
            }
        });


        // Obtenemos los componentes y registramos Listeners

        this.addFAB = findViewById(R.id.add_note_button);
        this.noteHostFragment = (NoteHostFragment) getFragmentManager().findFragmentById(R.id.notes_host_fragment);
        this.folderHostFragment = (FolderHostFragment) getFragmentManager().findFragmentById(R.id.folders_host_fragment);

        topAppBar.setOnMenuItemClickListener(this);
        folderHostFragment.registerChipListener(noteHostFragment);

        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), NotasActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });
    }

    /**
     * Implementación del método OnMenuItemClick para definir las acciones de los items del Toolbar.
     * @param item item del Toolbar: search, sandwich
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                searchNote();
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
     * Método para buscar una Nota en el Ámbito en el que estamos. TODO
     */
    private void searchNote() {}

    /**
     * Método para modificar el icono del MenuItem de cambiar vista de Notas
     * @param item menú item de cambiar vista de notas (sandwich)
     */
    private void changeCardNoteType(MenuItem item){
        cardNoteType = !cardNoteType;
        item.setIcon((cardNoteType) ? R.drawable.ic_baseline_table_rows_24 :
                R.drawable.ic_baseline_view_module_24);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){

        }
    }

    public void initHeaderNavigationView(String name, String eMail, int imgProfile){

        View header = (View) findViewById(R.id.headerView);

        TextView headerName = (TextView) header.findViewById(R.id.name);
        TextView headerEMail = (TextView) header.findViewById(R.id.email);
        CircleImageView headerImgProfile = header.findViewById(R.id.circleImageView);

        headerName.setText(name);
        headerEMail.setText(eMail);
        headerImgProfile.setImageResource(imgProfile);
    }

}