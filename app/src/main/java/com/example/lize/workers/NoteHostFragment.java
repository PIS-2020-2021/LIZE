package com.example.lize.workers;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.lize.R;
import com.example.lize.adapters.NoteAdapter;
import com.example.lize.data.Ambito;
import com.example.lize.data.Folder;
import com.example.lize.data.Note;
import com.example.lize.models.MainViewModel;
import java.util.ArrayList;


/** Notes View Host fragment. Responsabilidades:
 * <ol><li> Gestionar la parte de la UI correspondiente con el RecycleView de CardNotes </li>
 * <li> Definir la lógica del RecycleView mediante un {@link NoteAdapter}</li>
 * <li> Conectar el DataSet de Notas con el adaptador mediante un {@link com.example.lize.models.MainViewModel} </li></ol> */

public class NoteHostFragment extends Fragment implements NoteAdapter.CardNoteListener {

    private static final int REQUEST_CODE_EDIT_NOTE = 2;
    private Context mContext;                           // Root context
    private RecyclerView mNotesRecyclerView;            // Recycle View of Card-Notes
    private StaggeredGridLayoutManager mNotesManager;   // Recycle View Layout Manager
    private NoteAdapter mNoteAdapter;                   // NoteAdapter for the RecycleView
    private boolean cardNoteType;                       // boolean cardNote type
    private MainViewModel dataViewModel;                // Model Shared Data between Fragments
    private NoteAdapter.CardNote lastCardChecked;       // Last CardNote selected

    /** Inicializa el fragment contenedor de Notas. */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.notes_host_view, container, false);
        mContext = root.getContext();

        mNotesRecyclerView = root.findViewById(R.id.note_recycler_view);
        mNotesManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mNotesRecyclerView.setLayoutManager(mNotesManager);

        this.cardNoteType = true;
        return root;
    }

    /** Recuperamos la actividad que contiene este Fragmento para poder enlazarlo al MainViewModel */
    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        setHasOptionsMenu(true);
        mContext = root.getContext();

        dataViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Actualizamos la lista de Notas cuando se seleccione una Carpeta
        dataViewModel.getFolderSelected().observe(getViewLifecycleOwner(), (@Nullable Folder folder)->{
            if (dataViewModel.getViewUpdate().getValue()) {
                if (lastCardChecked != null) {
                    lastCardChecked.reset();
                    lastCardChecked = null;
                }
                try {
                    if (folder == null) {
                        Ambito ambito = dataViewModel.getAmbitoSelected().getValue();
                        mNoteAdapter = new NoteAdapter(root.getContext(),  new ArrayList<>(ambito.getNotes()), cardNoteType);
                    } else {
                        mNoteAdapter = new NoteAdapter(root.getContext(), new ArrayList<>(folder.getNotes()), cardNoteType);
                    }
                    mNoteAdapter.registerCardNoteListener(this);
                    mNotesRecyclerView.swapAdapter(mNoteAdapter, false);
                    mNoteAdapter.notifyDataSetChanged();

                } catch (NullPointerException exception) {
                    Log.w("NoteHostFragment", "Failed to update ambito's notes: null ambito selected.");
                    Log.w("NoteHostFragment", "Exception message: " + exception.getMessage());
                }
            }
            else {
                mNoteAdapter = new NoteAdapter(root.getContext(), new ArrayList<>(), cardNoteType);
                mNotesRecyclerView.swapAdapter(mNoteAdapter, false);
                mNoteAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Metodo para buscar notas
     * @param menuItem menu de busqueda
     */
    public void searchNote(MenuItem menuItem){
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mNoteAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    /**
     * Método para cambiar el tipo de los CardNotes:
     * Si cardNoteType = true; 2 columnas con cards de alto height {@link R.dimen# cardnote_layout_height_high}
     * Si cardNoteType = false; 1 columna con cards de bajo height {@link R.dimen# cardnote_layout_height_low}
     */
    public void changeCardNoteType(){
        this.cardNoteType = !cardNoteType;

        // Modificamos el num de columnas, a partir del LayoutManager.
        if (cardNoteType) mNotesManager.setSpanCount(2);
        else mNotesManager.setSpanCount(1);

        // Modificamos el 'type' de los CardNote de las notas, a partir del Adaptador.
        mNoteAdapter.changeCardNoteType(cardNoteType);
    }

    /**
     * Cuando un card note sea clickeado, inicia la actividad NotasActivity.class mediante un Intent
     * @param cardNote cardNote clickeado
     */
    @Override
    public void onCardNoteClicked(NoteAdapter.CardNote cardNote) {
        dataViewModel.selectNote(cardNote.getNoteID());
        Note selectedNote = dataViewModel.getNoteSelected().getValue();
        Intent intent = new Intent(mContext, NotasActivity.class);
        Bundle nota = new Bundle();

        nota.putString("title", selectedNote.getTitle());
        nota.putString("noteText_HTML", selectedNote.getText_html());
        nota.putString("documentsID",selectedNote.getDocumentsID());
        nota.putString("imagesID",selectedNote.getImagesID());
        nota.putBoolean("images",selectedNote.getHaveImages());
        nota.putBoolean("documents",selectedNote.getHaveDocuments());
        nota.putBoolean("audios",selectedNote.getHaveAudios());
        nota.putString("audiosID",selectedNote.getAudiosID());
        intent.putExtras(nota);

        requireActivity().startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
    }

    /**
     * Cuando un card note sea seleccionado, inicia una animación sobre el CardNote para mostrar un menú inferior.
     * Además, gestiona la lógica de deselección de los distintos CardNotes.
     * @param newCardNote cardNote seleccionado.
     */
    @Override
    public boolean onCardNoteSelected(NoteAdapter.CardNote newCardNote) {
        newCardNote.select();
        if (lastCardChecked != null && lastCardChecked != newCardNote) lastCardChecked.select();
        if (newCardNote.isSelected()) {
            dataViewModel.selectNote(newCardNote.getNoteID());
            lastCardChecked = newCardNote;
        } else lastCardChecked = null;
        return true;
    }

    /**
     * Cuando un card note sea seleccionado, inicia un mení sobre los CardNotes para mostrar los ambitos
     * y careptas a los que puede moverse dicho Card note.
     * @param cardNote cardNote seleccionado.
     */
    @Override
    public void onCardNoteMoved(NoteAdapter.CardNote cardNote) {
        View moveView = getLayoutInflater().inflate(R.layout.popup_move, null);
        MoveWindow popupWindow = new MoveWindow( mContext, moveView,
                dataViewModel.getUserSelected().getValue().getAmbitos());

        popupWindow.showAtLocation(requireView(), Gravity.CENTER, 0, 0);
        popupWindow.setWindowListener((ambitoID, folderID) -> {
            dataViewModel.moveNote(ambitoID, folderID, cardNote.getNoteID());
            popupWindow.dismiss();
        });
    }

    /**
     * Método listener para duplicar la nota seleccionada (cardNote).
     * @param cardNote cardNote seleccionado para duplicar.
     */
    @Override
    public void onCardNoteCopy(NoteAdapter.CardNote cardNote) {
        dataViewModel.copyNote(cardNote.getNoteID());
    }

    /**
     * Método para eliminar un CardNote de la colección de Notas de un Ámbito.
     * @param cardNote cardNote seleccionado para eliminar
     **/
    @Override
    public void onCardNoteDelete(NoteAdapter.CardNote cardNote) {
        dataViewModel.deleteNote(cardNote.getNoteID());
        lastCardChecked = null;
    }
}
