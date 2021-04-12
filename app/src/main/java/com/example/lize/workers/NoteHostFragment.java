package com.example.lize.workers;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.adapters.FolderAdapter;
import com.example.lize.adapters.NoteAdapter;
import com.example.lize.data.Folder;
import com.example.lize.data.Note;
import com.google.android.material.chip.Chip;

/** Fragment contenedor de Notas. Gestiona el RecycleView de CardNotes, sincronizandolo mediante un
 *  NoteAdapter. Además, modifica el NotaManager (GridLayoutManager) según el tipo de vista de las notas. */
public class NoteHostFragment extends Fragment implements FolderAdapter.ChipFolderListener {

    private Folder mFolder;                             // Model data
    private RecyclerView mNotesRecyclerView;            // Recycle View of Card-Notes
    private GridLayoutManager mNotesManager;            // Recycle View Layout Manager
    private NoteAdapter mNoteAdapter;                   // NoteAdapter for the RecycleView
    private boolean cardNoteType;                       // boolean cardNote type

    /** Inicializa el fragment contenedor de Notas. */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.notes_host_view, container, false);
        mFolder = new Folder("General");

        // Recycle View initiallization. By default, cardView enabled.
        mNotesRecyclerView = root.findViewById(R.id.note_recycler_view);
        mNotesManager = new GridLayoutManager(mNotesRecyclerView.getContext(), 2);
        mNotesRecyclerView.setLayoutManager(mNotesManager);
        this.cardNoteType = true;

        // Sets the NoteAdapter for the Recycle
        mNoteAdapter = new NoteAdapter(mNotesRecyclerView.getContext(), mFolder.getFolderNotes());
        mNotesRecyclerView.setAdapter(mNoteAdapter);

        //Get the data
        initializeData();
        return root;
    }

    /** Método para inicializar los DataSets a partir de los MOCKUPS definidos en strings.xml */
    private void initializeData() {
        //Get the resources from the XML file
        String[] notesNames = getResources().getStringArray(R.array.notes_names);
        String[] notesBody = getResources().getStringArray(R.array.notes_body);

        //Create the ArrayList of Notes objects with the titles and text data
        for(int i=0; i < notesNames.length; i++){
            mFolder.add( new Note(notesNames[i], notesBody[i]) );
        }

        //Notify the adapters of the changes
        mNoteAdapter.notifyDataSetChanged();
    }

    /**
     * Método para cambiar el tipo de los CardNotes:
     * Si cardNoteType = true; 2 columnas con cards de alto height {@link R.dimen#cardnote_layout_height_high}
     * Si cardNoteType = false; 1 columna con cards de bajo height {@link R.dimen#cardnote_layout_height_low}
     */
    public void changeCardNoteType(){
        this.cardNoteType = !cardNoteType;

        // Modificamos el num de columnas, a partir del LayoutManager.
        if (cardNoteType) mNotesManager.setSpanCount(2);
        else mNotesManager.setSpanCount(1);

        // Modificamos el 'type' de los CardNote de las notas, a partir del Adaptador.
        mNoteAdapter.changeCardNoteType();
    }

    /**
     * Cuando un folder chip sea clickeado, cambia el model folder y las notas expuestas mediante el adapter.
     * @param folder el chipFolder que ha sido clickeado.
     */
    @Override
    public void onChipClick(Chip folder) {
        // TODO: find a model Folder usign its name and change the mNoteAdapter dataSet
        mNoteAdapter.notifyDataSetChanged();
    }
}
