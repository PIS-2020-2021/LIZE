package com.example.lize.workers;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.adapters.MoveAmbitoAdapter;
import com.example.lize.adapters.MoveFolderAdapter;
import com.example.lize.adapters.MoveItem;
import com.example.lize.data.Ambito;
import com.example.lize.data.Folder;
import com.example.lize.utils.Preferences;

import java.util.ArrayList;

public class MoveWindow extends PopupWindow implements MoveAmbitoAdapter.MoveListener, MoveFolderAdapter.MoveListener {
    private final static int WIDTH = 900, HEIGHT = 700;

    private RecyclerView moveRecyclerView;
    private String ambitoID, folderName;
    private TextView mText;
    private Context mContext;

    private MoveItem ambitoSelected;
    private MoveWindowListener mListener;
    private ArrayList<Ambito> mAmbitoData;

    public interface MoveWindowListener{
        void moveNote(String ambitoID, String folderID);
    }

    public void setWindowListener(MoveWindowListener listener){
        mListener = listener;
    }

    public MoveWindow(Context mContext, View popupView, ArrayList<Ambito> mAmbitoData){
        super(popupView, WIDTH, HEIGHT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable());

        this.mContext = mContext;
        this.mText = popupView.findViewById(R.id.move_text);
        this.moveRecyclerView = popupView.findViewById(R.id.move_note_ambitos);
        this.mAmbitoData = mAmbitoData;
        moveRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        moveRecyclerView.setAdapter(new MoveAmbitoAdapter(mContext, mAmbitoData, this));
        ambitoSelected = new MoveItem(popupView.findViewById(R.id.move_ambito_selected));
    }

    @Override
    public void selectAmbito(Ambito ambito) {
        ambitoSelected.bindTo(ambito.getName() + " (Root)", ambito.getSelfID());
        if (ambito.getFolders().size() == 0) {
            selectFolder(null);
            return;
        }

        ambitoSelected.setIconVisibility(true);
        int colorResource = mContext.getResources().getColor(Preferences.getAmbitoPressedColor(ambito.getColor()));
        int defaultColorResource = mContext.getResources().getColor(Preferences.getDefaultAmbitoColor());
        ambitoSelected.setIconTint(colorResource);
        ambitoSelected.setDividerColor(defaultColorResource);
        ambitoSelected.setOnClickListener(v -> selectFolder(null));
        ambitoSelected.itemView.setVisibility(View.VISIBLE);
        mText.setText("Escoge una Carpeta:");

        MoveFolderAdapter folderAdapter = new MoveFolderAdapter(mContext, ambito.getFolders(), ambito.getColor(), this);
        moveRecyclerView.swapAdapter(folderAdapter, false);
        folderAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectFolder(Folder folder) {
        if (mListener != null) mListener.moveNote(ambitoSelected.getID(), (folder == null)? null : folder.getName());
    }

}


