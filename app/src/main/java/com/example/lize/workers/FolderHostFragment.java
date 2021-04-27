package com.example.lize.workers;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import com.example.lize.R;
import com.example.lize.adapters.FolderAdapter;
import com.example.lize.data.Ambito;
import com.example.lize.data.Folder;
import com.example.lize.models.MainViewModel;
import com.google.android.material.chip.Chip;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * Folder View Host fragment. Tiene las siguientes responsabilidades: <ol>
 * <li> Contener la parte de la UI del RecycleView de carpetas de un Ámbito {@link #mFoldersRecyclerView} </li>
 * <li> Definir su lógica mediante un {@link FolderAdapter}, y escucha al ChipFolder seleccionado. </li>
 * <li> Conectar el DataSet de Folders con el adaptador mediante la clase {@link MainViewModel} </li> </ol> */

public class FolderHostFragment extends Fragment implements FolderAdapter.ChipFolderListener{

    private Context mContext;                    // root context
    private Chip rootFolder;                     // Root Chip Folder selected
    private RecyclerView mFoldersRecyclerView;   // Recycle View of folders
    private FolderAdapter mFolderAdapter;        // FolderAdapter for the RecycleView

    private MainViewModel dataViewModel;        // Model Shared Data between Fragments

    /** Inicializa el Fragment contenedor de Folders */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.folders_host_view, container, false);
        this.mContext = root.getContext();
        this.rootFolder = root.findViewById(R.id.root_folder);
        this.mFoldersRecyclerView = root.findViewById(R.id.folder_recycler_view);
        mFoldersRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false));
        return root;
    }

    /** Recuperamos la actividad que contiene este Fragmento para poder enlazarlo al MainViewModel */
    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        this.mContext = root.getContext();

        this.dataViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        dataViewModel.getAmbitoSelected().observe(getViewLifecycleOwner(), (Ambito ambito) ->{
            mFolderAdapter = new FolderAdapter(mContext, ambito.getFolders());
            mFolderAdapter.registerChipFolderListener(this);
            mFoldersRecyclerView.swapAdapter(mFolderAdapter, false);
            mFolderAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Añadimos una nueva carpeta al DataSet del MainViewModel.
     * @param folderName Nombre de la nueva carpeta a crear
     */
    public void addFolderChip(String folderName) {
        dataViewModel.addFolder(folderName);
    }

    /**
     * Cuando un folder chip sea clickeado, cambia el root folder.
     * @param chipFolder el chipFolder que ha sido clickeado.
     */
    @Override
    public void onChipSelected(Chip chipFolder) {
        String folderName = chipFolder.getText().toString();
        String rootName = rootFolder.getText().toString();
        rootFolder.setText(folderName);
        chipFolder.setText(rootName);
        dataViewModel.selectFolder(folderName);
    }

}
