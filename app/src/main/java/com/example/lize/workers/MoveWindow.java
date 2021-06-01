package com.example.lize.workers;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.data.Ambito;
import com.example.lize.data.Folder;
import com.example.lize.utils.Preferences;

import java.util.ArrayList;

/**  Window popup para encapsular la lógica de mover una Nota a un Ámbito y Carpeta de destino. */
public class MoveWindow extends PopupWindow {
    private final static int WIDTH = 900, HEIGHT = 700;
    private final Context mContext;
    private final TextView mText;
    private final RecyclerView moveRecyclerView;
    private Ambito ambitoSelected;
    private final MoveWindow.MoveItem itemSelected;
    private MoveWindowListener mListener;

    /**
     * Interfaz del MoveWindow
     */
    public interface MoveWindowListener {
        void moveNote(String ambitoID, String folderID);
    }

    /**
     * Metodo para establecer el Listener del MoveWindow
     * @param listener Listener del MoveWindow
     */
    public void setWindowListener(MoveWindowListener listener) { mListener = listener; }

    /**
     * Constructor de la clase
     * @param mContext Contexto del MoveWindow
     * @param popupView View del popup
     * @param mAmbitoData Lista de Ambitos
     */
    public MoveWindow(Context mContext, View popupView, ArrayList<Ambito> mAmbitoData) {
        super(popupView, WIDTH, HEIGHT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable());

        this.mContext = mContext;
        this.mText = popupView.findViewById(R.id.move_text);
        this.itemSelected = new MoveItem(popupView.findViewById(R.id.move_ambito_selected));
        this.moveRecyclerView = popupView.findViewById(R.id.move_note_ambitos);

        moveRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        moveRecyclerView.setAdapter(new MoveAmbitoAdapter(mAmbitoData));
    }

    /**
     * Seleccionamos un Ámbito como el destino. Si no contiene Folders, llamamos al Listener.
     * Si contiene Folders, cambiamos el RecyclerView para pedir la selección de Folder.
     * @param ambito Ambito seleccionado
     */
    public void selectAmbito(Ambito ambito) {
        this.ambitoSelected = ambito;
        if (ambitoSelected.getFolders().size() == 0 && mListener != null) {
            mListener.moveNote(ambitoSelected.getSelfID(), null);
            return;
        }

        int color = mContext.getResources().getColor(Preferences.getAmbitoPressedColor(ambitoSelected.getColor()));
        int defaultColor = mContext.getResources().getColor(Preferences.getDefaultAmbitoColor());
        itemSelected.bindToFolder(ambito.getName() + " (Root)", color, defaultColor);
        itemSelected.itemView.setVisibility(View.VISIBLE);

        mText.setText("Escoge una Carpeta:");

        MoveFolderAdapter folderAdapter = new MoveFolderAdapter(ambito.getFolders());
        moveRecyclerView.swapAdapter(folderAdapter, false);
        folderAdapter.notifyDataSetChanged();
    }

    /**
     * Selección de la Folder de destino. Puede ser la Folder general de un Ámbito o no.
     * @param foldername Carpeta seleccionada
     */
    public void selectFolder(String foldername) {
        if (mListener != null) {
            if (foldername.equals(ambitoSelected.getName() + " (Root)")) mListener.moveNote(ambitoSelected.getSelfID(), null);
            else mListener.moveNote(ambitoSelected.getSelfID(), foldername);
        }
    }

    // Adapter para enlazar los Ámbitos con los MoveItems del RecyclerView.
    private class MoveAmbitoAdapter extends RecyclerView.Adapter<MoveWindow.MoveItem> {
        private final ArrayList<Ambito> mAmbitosData;

        /**
         * Constructor de la clase
         * @param ambitos lista de Ambitos
         */
        public MoveAmbitoAdapter(ArrayList<Ambito> ambitos) { this.mAmbitosData = ambitos; }

        @NonNull
        @Override
        public MoveWindow.MoveItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MoveWindow.MoveItem(LayoutInflater.from(mContext).inflate(R.layout.item_move, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MoveWindow.MoveItem item, int position) {
            Ambito ambito = mAmbitosData.get(position);
            int colorResource = mContext.getResources().getColor(Preferences.getAmbitoPressedColor(ambito.getColor()));
            item.bindToAmbito(ambito, colorResource);
        }

        @Override
        public int getItemCount() { return mAmbitosData.size(); }

    }

    // Adapter para enlazar las Folders del Ámbito Seleccionado con los MoveItems del RecyclerView.
    private class MoveFolderAdapter extends RecyclerView.Adapter<MoveWindow.MoveItem> {
        private final ArrayList<Folder> mFoldersData;

        /**
         * Constructor de la clase
         * @param folders Lista de Carpetas
         */
        public MoveFolderAdapter(ArrayList<Folder> folders) { this.mFoldersData = folders; }

        @NonNull
        @Override
        public MoveWindow.MoveItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MoveWindow.MoveItem(LayoutInflater.from(mContext).inflate(R.layout.item_move, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MoveWindow.MoveItem item, int position) {
            Folder folder = mFoldersData.get(position);
            int color = mContext.getResources().getColor(Preferences.getAmbitoPressedColor(ambitoSelected.getColor()));
            int defaultColor = mContext.getResources().getColor(Preferences.getDefaultAmbitoColor());
            item.bindToFolder(folder.getName(), color, defaultColor);
        }

        @Override
        public int getItemCount() { return mFoldersData.size(); }

    }

    // MoveItem. Consta de un simple TextView, un Divisor de color y un icono (visualizable o no)
    private class MoveItem extends RecyclerView.ViewHolder {
        private final TextView mTitle;
        private final View mDivider;
        private final ImageView mIcon;

        /**
         * Constructor de la clase
         * @param itemView View a mover
         */
        public MoveItem(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.item_text);
            mDivider = itemView.findViewById(R.id.item_divider);
            mIcon = itemView.findViewById(R.id.item_icon);
        }

        /**
         * Metodo para establecer el Item en su nuevo Ambito
         * @param ambito Ambito de destino
         * @param color Color del Ambito de destino
         */
        public void bindToAmbito(Ambito ambito, int color){
            mTitle.setText(ambito.getName());
            mIcon.setVisibility(View.GONE);
            mDivider.setBackgroundColor(color);
            itemView.setOnClickListener(v -> selectAmbito(ambito));
        }

        /**
         * Metodo para establecer el Item en su nueva Carpeta
         * @param folderName Carpeta de destino
         * @param color Color del Ambito de destino
         * @param defaultColor Color default
         */
        public void bindToFolder(String folderName, int color, int defaultColor) {
            mTitle.setText(folderName);
            mIcon.setVisibility(View.VISIBLE);
            mIcon.setColorFilter(color);
            mDivider.setBackgroundColor(defaultColor);
            itemView.setOnClickListener(v -> selectFolder(folderName));
        }
    }

}


