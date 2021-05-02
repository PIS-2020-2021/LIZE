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
import com.example.lize.models.MainViewModel;
import com.google.android.material.chip.Chip;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Folder View Host fragment. Tiene las siguientes responsabilidades: <ol>
 * <li> Contener la parte de la UI del RecycleView de carpetas de un Ámbito {@link #mFoldersRecyclerView} </li>
 * <li> Definir su lógica mediante un {@link FolderAdapter}, y escucha al ChipFolder seleccionado. </li>
 * <li> Conectar el DataSet de Folders con el adaptador mediante la clase {@link MainViewModel} </li> </ol> */

//TODO: Refactorizar el ChipRoot Folder (Layout)
public class FolderHostFragment extends Fragment implements FolderAdapter.ChipFolderListener{

    private Context mContext;                    // root context
    private RecyclerView mFoldersRecyclerView;   // Recycle View of folders
    private FolderAdapter mFolderAdapter;        // FolderAdapter for the RecycleView
    private Chip selectedFolder;                 // Chip Folder selected
    private TypedValue typedValue;               // For color wrapping

    private MainViewModel dataViewModel;        // Model Shared Data between Fragments


    /** Inicializa el Fragment contenedor de Folders */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.folders_host_view, container, false);
        this.mContext = root.getContext();
        typedValue = new TypedValue();
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

        // Si la carpeta seleccionada es la Folder
        /*dataViewModel.getFolderSelected().observe(getViewLifecycleOwner(), (Folder folder) ->{
            if (folder.getName().equals(Folder.BASE_FOLDER_NAME)){
                // 1. Trobar el Chip corresponent a la BASE_FOLDER_NAME

                for (int i = 0; i < mFolderAdapter.getItemCount(); i++){
                    Chip chipFolder = (Chip) mFoldersRecyclerView.getLayoutManager().getChildAt(i);
                    if (chipFolder.getText().toString().equals())
                }getLayoutManager().get)
                // 2. Aplicar un onChipSelected() de la BaseFolderName
            }
            mFolderAdapter = new FolderAdapter(mContext, ambito.getFolders());
            mFolderAdapter.registerChipFolderListener(this);
            mFoldersRecyclerView.swapAdapter(mFolderAdapter, false);
            mFolderAdapter.notifyDataSetChanged();
        });*/

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
        changeColorLogic(selectedFolder, chipFolder);
        selectedFolder = chipFolder;
        dataViewModel.selectFolder(folderName);
    }

    // Lógica de cambio de color de selección
    private void changeColorLogic(Chip selectedFolder, Chip chipFolder) {
        mContext.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        chipFolder.setChipBackgroundColor(ContextCompat.getColorStateList(mContext, typedValue.resourceId));

        mContext.getTheme().resolveAttribute(R.attr.colorOnSurface, typedValue, true);
        chipFolder.setTextColor(ContextCompat.getColor(mContext, typedValue.resourceId));

        String currentFolderName = chipFolder.getText().toString();

        // Si ya habíamos seleccionado un ChipFolder y es distinto al nuevo, lo deseleccionamos
        if (selectedFolder != null && !(currentFolderName.equals(selectedFolder.getText().toString()))){

            mContext.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            selectedFolder.setChipBackgroundColor(ContextCompat.getColorStateList(mContext, typedValue.resourceId));

            mContext.getTheme().resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
            selectedFolder.setTextColor(ContextCompat.getColor(mContext, typedValue.resourceId));
        }
    }


    /*
     * Cuando enlazemos el folder chip correspondiente a la BaseFolder por primera vez, lo seleccionamos.
     * @param rootFolder el rootFolder que ha sido enlazado */
    /*public void rootChipSelected(Chip rootFolder){
        if (this.selectedFolder == null){
            this.selectedFolder = rootFolder;
            selectedFolder.setTextAppearance(R.style.Widget_Lize_Chip);
        }
    }*/

}
