package com.example.lize.workers;

import androidx.annotation.Nullable;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.fragment.app.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.lize.R;
import com.example.lize.adapters.NoteAdapter;
import com.example.lize.data.Ambito;
import com.example.lize.data.Folder;
import com.example.lize.data.Note;
import com.example.lize.models.MainViewModel;
import com.google.android.material.card.MaterialCardView;


/** Notes View Host fragment. Responsabilidades:
 * <ol><li> Gestionar la parte de la UI correspondiente con el RecycleView de CardNotes </li>
 * <li> Definir la lógica del RecycleView mediante un {@link NoteAdapter}</li>
 * <li> Conectar el DataSet de Notas con el adaptador mediante un {@link com.example.lize.models.MainViewModel} </li></ol> */

public class NoteHostFragment extends Fragment implements NoteAdapter.CardNoteListener {

    private static final int REQUEST_CODE_EDIT_NOTE = 2;

    private Context mContext;                               // Root context
    private RecyclerView mNotesRecyclerView;                // Recycle View of Card-Notes
    private StaggeredGridLayoutManager mNotesManager;       // Recycle View Layout Manager
    private NoteAdapter mNoteAdapter;                       // NoteAdapter for the RecycleView

    private NoteAdapter.CardNote lastCardChecked;           // Last CardNote selected
    private boolean cardNoteType;                           // boolean cardNote type

    private MainViewModel dataViewModel;                    // Model Shared Data between Fragments

    /** Inicializa el fragment contenedor de Notas. */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.notes_host_view, container, false);
        mContext = root.getContext();

        mNotesRecyclerView = root.findViewById(R.id.note_recycler_view);
        mNotesManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //mNotesManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
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

        // Actualizamos la lista de Notas cuando se seleccione una Carpeta
        dataViewModel.getFolderSelected().observe(getViewLifecycleOwner(), (@Nullable Folder folder)->{
            if (((MainActivity) requireActivity()).isThemeUpdated()) {

                if (lastCardChecked != null) {
                    lastCardChecked.reset();
                    lastCardChecked = null;
                }
                try {
                    if (folder == null) {
                        Ambito ambito = dataViewModel.getAmbitoSelected().getValue();
                        mNoteAdapter = new NoteAdapter(root.getContext(), ambito.getNotes(), cardNoteType);
                    } else
                        mNoteAdapter = new NoteAdapter(root.getContext(), folder.getNotes(), cardNoteType);

                    mNoteAdapter.registerCardNoteListener(this);
                    mNotesRecyclerView.swapAdapter(mNoteAdapter, false);
                    mNoteAdapter.notifyDataSetChanged();

                } catch (NullPointerException exception) {
                    Log.w("NoteHostFragment", "Failed to update ambito's notes: null ambito selected.");
                    Log.w("NoteHostFragment", "Exception message: " + exception.getMessage());
                }
            }
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
     * Añadimos una nueva nota al DataSet del MainViewModel.
     * @param noteName Nombre de la nueva nota a crear
     * @param text_plain Texto Plano de la nueva nota a crear
     * @param text_html Texto HTML de la nueva nota a crear
     */
    public void addCardNote(String noteName, String text_plain, String text_html) {
        dataViewModel.addNote(noteName, text_plain, text_html);
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
        intent.putExtras(nota);

        requireActivity().startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
    }

    /**
     * Cuando un card note sea seleccionado, inicia una animación sobre el CardNote para mostrar un menú inferior.
     * Además, gestiona la lógica de deselección de los distintos CardNotes.
     * @param newCardNote cardNote seleccionado.
     */
    @Override
    public void onCardNoteSelected(NoteAdapter.CardNote newCardNote) {
        if (lastCardChecked != null && lastCardChecked != newCardNote) lastCardChecked.reset();
        if (newCardNote.isSelected()) lastCardChecked = newCardNote;
        else lastCardChecked = null;
    }

    //TODO: Implement CardNote #MOVE, #COPY & #DELETE operations
    @Override
    public void onCardNoteMoved(NoteAdapter.CardNote cardNote) {

    }

    @Override
    public void onCardNoteCopy(NoteAdapter.CardNote cardNote) {

    }

    @Override
    public void onCardNoteDelete(NoteAdapter.CardNote cardNote) {

    }

}
