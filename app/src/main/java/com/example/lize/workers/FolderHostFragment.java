package com.example.lize.workers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import com.example.lize.R;
import com.example.lize.adapters.FolderAdapter;
import com.example.lize.data.Folder;
import com.example.lize.data.Note;
import com.google.android.material.chip.Chip;

import android.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/** Folder View Host fragment. Contiene el RecycleView de las carpetas del Ámbito. */
public class FolderHostFragment extends Fragment implements FolderAdapter.ChipFolderListener{

    private ArrayList<Folder> mFoldersData;      // Model Data
    private View root;                           // Main Activity
    private RecyclerView mFoldersRecyclerView;   // Recycle View of folders
    private FolderAdapter mFolderAdapter;        // FolderAdapter for the RecycleView
    private Chip rootFolder;                     // General folder selected

    /** Inicializa el fragment contenedor de folders. */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.folders_host_view, container, false);

        // Inicializamos el RecycleView con su Manager y su Adapter.
        mFoldersRecyclerView = root.findViewById(R.id.folder_recycler_view);
        mFoldersRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        //Initialize the ArrayLists containing the data
        mFoldersData = new ArrayList<>();

        //Initialize the adapters and sets them to the RecyclerViews
        mFolderAdapter = new FolderAdapter(root.getContext(), mFoldersData);
        mFolderAdapter.registerChipFolderListener(this);
        mFoldersRecyclerView.setAdapter(mFolderAdapter);

        // General Chip Folder
        rootFolder = root.findViewById(R.id.root_folder);

        //Get the data
        initializeData();
        return root;
    }

    /** Método para inicializar los DataSets a partir de los MOCKUPS definidos en strings.xml */
    private void initializeData() {
        //Get the resources from the XML file
        String[] notesNames = getResources().getStringArray(R.array.notes_names);
        String[] notesBody = getResources().getStringArray(R.array.notes_body);
        Folder general = new Folder("General");
        Folder folder1 = new Folder("Folder1");
        Folder folder2 = new Folder("Folder2");

        //Create the ArrayList of Notes objects with the titles and text data
        for(int i=0; i < notesNames.length; i++){
            Note newNote = new Note(notesNames[i], notesBody[i]);
            general.add(newNote);
            if(i < 5) folder1.add(newNote);
            else folder2.add(newNote);
        }

        //Create the ArrayList of Folder objects with their names and notes
        ArrayList<Folder> folders = new ArrayList<>();
        mFoldersData.add(folder1);
        mFoldersData.add(folder2);
        mFoldersData.add(folder2);
        mFoldersData.add(folder2);

        //Notify the adapters of the changes
        mFolderAdapter.notifyDataSetChanged();
    }

    /** Método 'bypass' para registrar otras vistas como ChipFolderListeners del Adaptador de esta clase */
    public void registerChipListener(FolderAdapter.ChipFolderListener listener){
        mFolderAdapter.registerChipFolderListener(listener);
    }

    /**
     * Cuando un folder chip sea clickeado, cambia el root folder.
     * @param folder el chipFolder que ha sido clickeado.
     */
    @Override
    public void onChipClick(Chip folder) {
        // Change folder chips: the root is the chip clicked, and the clicked will be the root.
        String rootFolderText = (String) rootFolder.getText();
        String chipFolderText = (String) folder.getText();
        rootFolder.setText(chipFolderText);
        folder.setText(rootFolderText);
    }
}
