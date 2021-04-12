package com.example.lize.workers;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.View;

import com.example.lize.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

;

/** Activity Principal de la app Lize. Contenedor del Ámbito con sus Carpetas y sus Notas. */
public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener{

    private MaterialToolbar topAppBar;                       // MaterialToolbar de la app.
    private NoteHostFragment noteHostFragment;               // Contenedor de Notas
    private FolderHostFragment folderHostFragment;           // Contenedor de Folders
    private FloatingActionButton addFAB;                     // Floating Action Button de añadir
    private boolean cardNoteType = true;                     // Boolean del tipo de vista de las Notas.
    public static final int REQUEST_CODE_ADD_NOTE = 1;

    /** Main constructor */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtenemos los componentes y registramos Listeners
        this.topAppBar = findViewById(R.id.ambito_material_toolbar);
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

}