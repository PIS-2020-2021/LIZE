package com.example.lize.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.data.Folder;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

/**
 * Adaptador del RecyclerView de Carpetas, en la actividad principal. Enlaza los datos del dataSet
 * de Carpetas con el correspondiente ChipFolder (ViewHolder).
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ChipFolder>{

    private final Context mContext;
    private final ArrayList<Folder> mFoldersData;
    private final ArrayList<ChipFolderListener> chipListeners;

    /* Custom ChipFolder onClick Listener */
    public interface ChipFolderListener{ void onFolderSelected(Folder f);}

    /**
     * Method for registering a ChipFolder onClick listener
     * @param listener Observer which knows when the chip is clicked.
     */
    public void registerChipFolderListener(ChipFolderListener listener){ chipListeners.add(listener); }

    /**
     * Constructor que pasa el listado de carpetas i el contexto.
     * @param context contexto de la app
     * @param foldersData ArrayList conteniendo la información de las carpetas.
     */
    public FolderAdapter(Context context, ArrayList<Folder> foldersData) {
        this.mFoldersData = foldersData;
        this.mContext = context;
        this.chipListeners = new ArrayList<>();
    }

    /**
     * Generador de ViewHolders de carpetas.
     * @param parent ViewGroup correspondiente al RecycleView - contenedor de carpetas.
     * @param viewType Tipo de vista del ViewHolder
     * @return El nuevo ViewHolder
     */
    @NonNull
    @Override
    public FolderAdapter.ChipFolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderAdapter.ChipFolder(LayoutInflater.from(mContext).inflate(R.layout.folder_chip, parent, false));
    }

    /**
     * Método requerido que permite enlazar los datos de la carpeta con el correspondiente ViewHolder.
     * @param holder ViewHolder a quien pasar los datos.
     * @param position Position del adaptador.
     */
    @Override
    public void onBindViewHolder(@NonNull ChipFolder holder, int position) {
        Folder currentFolder = mFoldersData.get(position); // Obtenemos la carpeta de la posición
        holder.bindTo(currentFolder); // Enlazamos la carpeta con el ViewHolder
    }

    /**
     * Método que requiere el adaptador para determinar el tamaño del dataSet
     * @return tamaño DataSet
     */
    @Override
    public int getItemCount() { return mFoldersData.size(); }

    /**
     * ViewHolder class que se corresponde con los Chips de las carpetas
     */
    public class ChipFolder extends RecyclerView.ViewHolder{

        private Chip folderChip;
        private Folder mFolder;

        /**
         * Constructor del ViewHolder correspondiente al layout de folder_chip
         * @param itemView rootview del fichero folder_chip.xml
         */
        public ChipFolder(View itemView) {
            super(itemView);
            this.folderChip = (Chip) itemView;
            folderChip.setOnClickListener((v)->{
                if (mFolder != null) {
                    for (ChipFolderListener listener : chipListeners)
                        listener.onFolderSelected(mFolder);
                }
            });
        }

        /**
         * Método para <b>enlazar</b> los datos de la carpeta con el Chip de este objeto ViewHolder.
         * @param currentFolder carpeta actual
         */
        public void bindTo(Folder currentFolder) {
            mFolder = currentFolder;
            folderChip.setText(currentFolder.getFolderName());
        }
    }
}
