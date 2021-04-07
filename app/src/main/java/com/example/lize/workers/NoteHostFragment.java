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
import com.example.lize.adapters.NoteAdapter;
import com.example.lize.data.Folder;
import com.example.lize.data.Note;

public class NoteHostFragment extends Fragment {

    private Folder mFolder;                     // Model data
    private RecyclerView mNotesRecyclerView;    // Recycle View of Card-Notes
    private NoteAdapter mNoteAdapter;           // NoteAdapter for the RecycleView

    /**
     * Inflates the RecyclerView for the Notes.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mNotesRecyclerView = (RecyclerView) inflater.inflate(R.layout.notes_host_view, container, false);
        mFolder = new Folder("General");

        mNotesRecyclerView.setLayoutManager(new GridLayoutManager(mNotesRecyclerView.getContext(), 2));

        // Sets a NoteAdapter for the Recycle
        mNoteAdapter = new NoteAdapter(mNotesRecyclerView.getContext(), mFolder.getFolderNotes());
        mNotesRecyclerView.setAdapter(mNoteAdapter);

        //Get the data
        initializeData();
        return mNotesRecyclerView;
    }

    /**
     * Método para inicializar los DataSets a partir de los MOCKUPS definidos en strings.xml
     */
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

}
