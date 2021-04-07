package com.example.lize.workers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import com.example.lize.R;
import com.example.lize.adapters.FolderAdapter;
import com.example.lize.adapters.NoteAdapter;
import com.example.lize.data.Folder;
import com.example.lize.data.Note;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Folder View Host fragment. Contains the RecycleView of the ambit folders.
 */
public class FolderHostFragment extends Fragment {

    private ArrayList<Folder> mFoldersData;     // Model Data
    private View root;                          // Main Activity
    private RecyclerView mFoldersRecyclerView;  // Recycle View of folders
    private FolderAdapter mFolderAdapter;           // FolderAdapter for the RecycleView

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.folders_host_view, container, false);

        //Initialize the RecyclerViews
        mFoldersRecyclerView = (RecyclerView) root.findViewById(R.id.folder_recycler_view);

        //Set the Layout Manager for both Recyclers
        mFoldersRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        //Initialize the ArrayLists containing the data
        mFoldersData = new ArrayList<>();

        //Initialize the adapters and sets them to the RecyclerViews
        mFolderAdapter = new FolderAdapter(root.getContext(), mFoldersData);

        mFoldersRecyclerView.setAdapter(mFolderAdapter);

        //Get the data
        initializeData();
        return root;
    }

    /**
     * Método para inicializar los DataSets a partir de los MOCKUPS definidos en strings.xml
     */
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
        mFoldersData.add(general);
        mFoldersData.add(folder1);
        mFoldersData.add(folder2);
        mFoldersData.add(folder2);
        mFoldersData.add(folder2);

        //Notify the adapters of the changes
        mFolderAdapter.notifyDataSetChanged();
    }
}
