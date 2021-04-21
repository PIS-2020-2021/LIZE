package com.example.lize.workers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import com.example.lize.R;
import com.example.lize.adapters.FolderAdapter;
import com.example.lize.data.Ambito;
import com.example.lize.models.AmbitoViewModel;
import com.example.lize.models.FolderViewModel;
import com.example.lize.data.Folder;
import com.google.android.material.chip.Chip;

import androidx.fragment.app.Fragment;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Folder View Host fragment. Tiene las siguientes responsabilidades: <ol>
 * <li> Contener la parte de la UI del RecycleView de carpetas de un Ámbito {@link #mFoldersRecyclerView} </li>
 * <li> Definir su lógica mediante un {@link FolderAdapter}, y escucha al ChipFolder seleccionado. </li>
 * <li> Conectar el DataSet de Folders con el adaptador mediante la clase {@link FolderViewModel} </li> </ol> */

public class FolderHostFragment extends Fragment implements FolderAdapter.ChipFolderListener{

    private Chip rootFolder;                     // General folder selected
    private RecyclerView mFoldersRecyclerView;   // Recycle View of folders
    private FolderAdapter mFolderAdapter;        // FolderAdapter for the RecycleView

    private AmbitoViewModel mAmbitoViewModel;    // Selected Ambito Observation
    private FolderViewModel mFolderViewModel;    // Folder selection

    /** Inicializa el Fragment contenedor de Folders */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.folders_host_view, container, false);
        this.rootFolder = root.findViewById(R.id.root_folder);
        this.mFoldersRecyclerView = root.findViewById(R.id.folder_recycler_view);
        mFoldersRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        return root;
    }

    /** Recuperamos la actividad que contiene este Fragmento para poder enlazarlo al FolderViewModel */
    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        mAmbitoViewModel = new ViewModelProvider(requireActivity()).get(AmbitoViewModel.class);
        mFolderViewModel = new ViewModelProvider(requireActivity()).get(FolderViewModel.class);

        // Implementación del Observador del Ambito Seleccionado
        mAmbitoViewModel.getAmbitoSelected().observe(getViewLifecycleOwner(), (Ambito ambito)->{
            mFolderAdapter = new FolderAdapter(root.getContext(), ambito.getAmbitoFolders());
            mFolderAdapter.registerChipFolderListener(this);
            onFolderSelected(ambito.getAmbitoFolders().get(0));
            mFoldersRecyclerView.swapAdapter(mFolderAdapter, false);
            mFolderAdapter.notifyDataSetChanged();
        });

        // Implementación del Observador del String del Toast
        mFolderViewModel.getToast().observe(getViewLifecycleOwner(), (String t)->{
            Toast.makeText(root.getContext(), t, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Añadimos una nueva carpeta al DataSet del AmbitoViewModel.
     * @param folderName Nombre de la nueva carpeta a crear
     */
    public void addFolderChip(String folderName) {
        mAmbitoViewModel.addFolder(folderName);
    }

    /**
     * Cuando un folder chip sea clickeado, cambia el root folder.
     * @param folder el chipFolder que ha sido clickeado.
     */
    @Override
    public void onFolderSelected(Folder folder) {
        // TODO: Change folder chips!
        rootFolder.setText(folder.getFolderName());
        mFolderViewModel.setFolderSelected(folder);
    }



}
