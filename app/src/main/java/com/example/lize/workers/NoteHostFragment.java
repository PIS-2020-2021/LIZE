package com.example.lize.workers;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.adapters.NoteAdapter;
import com.example.lize.data.Folder;
import com.example.lize.data.Note;
import com.example.lize.models.FolderViewModel;
import com.example.lize.models.MainViewModel;
import com.example.lize.models.NoteViewModel;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

/** Notes View Host fragment. Responsabilidades:
 * <ol><li> Gestionar la parte de la UI correspondiente con el RecycleView de CardNotes </li>
 * <li> Definir la lógica del RecycleView mediante un {@link NoteAdapter}</li>
 * <li> Conectar el DataSet de Notas con el adaptador mediante un {@link com.example.lize.models.MainViewModel} </li></ol> */
public class NoteHostFragment extends Fragment implements NoteAdapter.CardNoteListener {

    private Context mContext;                           // Root context
    private RecyclerView mNotesRecyclerView;            // Recycle View of Card-Notes
    private GridLayoutManager mNotesManager;            // Recycle View Layout Manager
    private NoteAdapter mNoteAdapter;                   // NoteAdapter for the RecycleView
    private boolean cardNoteType;                       // boolean cardNote type

    private MainViewModel dataViewModel;                // Model Shared Data between Fragments

    /** Inicializa el fragment contenedor de Notas. */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.notes_host_view, container, false);
        mContext = root.getContext();
        // Recycle View initiallization. By default, cardView enabled.
        mNotesRecyclerView = root.findViewById(R.id.note_recycler_view);
        mNotesManager = new GridLayoutManager(mContext, 2);
        mNotesRecyclerView.setLayoutManager(mNotesManager);
        this.cardNoteType = true;
        return root;
    }

    /** Recuperamos la actividad que contiene este Fragmento para poder enlazarlo al MainViewModel */
    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        mContext = root.getContext();

        dataViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        dataViewModel.getFolderSelected().observe(getViewLifecycleOwner(), (Folder folder)->{
            mNoteAdapter = new NoteAdapter(root.getContext(), folder.getFolderNotes());
            mNoteAdapter.registerCardNoteListener(this);
            mNotesRecyclerView.swapAdapter(mNoteAdapter, false);
            mNoteAdapter.notifyDataSetChanged();
        });

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
     * Añadimos una nueva nota al DataSet del FolderViewModel.
     * @param noteName Nombre de la nueva nota a crear
     * @param noteText Texto de la nueva nota a crear
     */
    public void addCardNote(String noteName, String noteText, String folderID) {
        dataViewModel.addNote(noteName, noteText, folderID);
    }

    /**
     * Cuando un card note sea clickeado, inicia la actividad NotasActivity.class mediante un Intent
     * @param note el cardNote que ha sido clickeado.
     */
    @Override
    public void onNoteSelected(Note note) {
        //TODO: startActivityForResult(new Intent(getApplicationContext(), NotasActivity.class), REQUEST_CODE_ADD_NOTE);
    }
}
