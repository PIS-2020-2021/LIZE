package com.example.lize.workers;


import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
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
import com.google.android.material.chip.ChipGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/**
 * Folder View Host fragment. Tiene las siguientes responsabilidades: <ol>
 * <li> Contener la parte de la UI del RecycleView de carpetas de un Ámbito {@link #mFoldersRecyclerView} </li>
 * <li> Definir su lógica mediante un {@link FolderAdapter}, y escucha al ChipFolder seleccionado. </li>
 * <li> Conectar el DataSet de Folders con el adaptador mediante la clase {@link MainViewModel} </li> </ol> */

public class FolderHostFragment extends Fragment implements FolderAdapter.ChipFolderListener{

    private Context mContext;                    // Root context
    private RecyclerView mFoldersRecyclerView;   // Recycle View of folders
    private FolderAdapter mFolderAdapter;        // FolderAdapter for the RecycleView
    private Chip selectedFolder;                 // Selected ChipFolder

    private MainViewModel dataViewModel;         // Model Shared Data between Fragments


    /** Inicializa el Fragment contenedor de Folders */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.folders_host_view, container, false);
        this.mContext = root.getContext();
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

        // Actualizamos la lista de Carpetas cuando se seleccione un Ámbito
        dataViewModel.getAmbitoSelected().observe(getViewLifecycleOwner(), (@NonNull Ambito ambito) ->{
            mFolderAdapter = new FolderAdapter(mContext, ambito.getFolders(), this);
            mFoldersRecyclerView.swapAdapter(mFolderAdapter, false);
            mFolderAdapter.notifyDataSetChanged();
        });

        // Cuando deseleccionamos la Carpeta (folder == null), deseleccionamos el ChipFolder anterior.
        dataViewModel.getFolderSelected().observe(getViewLifecycleOwner(), (@Nullable Folder folder)->{
            if (folder == null) {
                if (selectedFolder != null) selectedFolder.setChecked(false);
                selectedFolder = null;
            }
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
     * Cuando un folder chip sea clickeado, seleccionamos / deseleccionamos la carpeta de persistencia
     * del {@link MainViewModel} mediante el método {@link MainViewModel#selectFolder(String)} /
     * {@link MainViewModel#deselectFolder()}.
     * @param chipFolder chipFolder seleccionado / deseleccionado.
     */
    @Override
    public void onChipSelected(Chip chipFolder) {
        if (chipFolder.isChecked()) {
            dataViewModel.selectFolder(chipFolder.getText().toString());
            if (selectedFolder != null && selectedFolder != chipFolder) selectedFolder.setChecked(false);
            selectedFolder = chipFolder;

        } else {
            dataViewModel.deselectFolder();
            selectedFolder = null;
        }
    }



    /* Métodos deprecated de cambio de color. Misma funcionalidad mediante selectores de color.
    private void selectColorChange(Chip selectedChip){
        mContext.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        selectedChip.setChipBackgroundColor(ContextCompat.getColorStateList(mContext, typedValue.resourceId));

        mContext.getTheme().resolveAttribute(R.attr.colorOnSurface, typedValue, true);
        selectedChip.setTextColor(ContextCompat.getColor(mContext, typedValue.resourceId));
    }

    // Bajamos el color del ChipFolder deseleccionado
    private void deselectColorChange(Chip deselectedChip){
        mContext.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        selectedFolder.setChipBackgroundColor(ContextCompat.getColorStateList(mContext, typedValue.resourceId));

        mContext.getTheme().resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
        selectedFolder.setTextColor(ContextCompat.getColor(mContext, typedValue.resourceId));
    }*/

}
