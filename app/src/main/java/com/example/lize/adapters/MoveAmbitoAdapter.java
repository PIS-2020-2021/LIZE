package com.example.lize.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.data.Ambito;
import com.example.lize.utils.Preferences;

import java.util.ArrayList;

public class MoveAmbitoAdapter extends RecyclerView.Adapter<MoveItem> {

    private final Context mContext;
    private final ArrayList<Ambito> mAmbitosData;
    private final MoveListener mMoveListener;

    public interface MoveListener{
        void selectAmbito(Ambito ambito);
    }

    public MoveAmbitoAdapter(Context context, ArrayList<Ambito> ambitos, MoveListener listener) {
        this.mAmbitosData = ambitos;
        this.mContext = context;
        mMoveListener = listener;
    }

    @NonNull
    @Override
    public MoveItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MoveItem(LayoutInflater.from(mContext).inflate(R.layout.item_move, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MoveItem item, int position) {
        Ambito ambito = mAmbitosData.get(position);
        int colorResource = mContext.getResources().getColor(Preferences.getAmbitoPressedColor(ambito.getColor()));
        item.bindTo(ambito.getName(), ambito.getSelfID());
        item.setDividerColor(colorResource);
        item.setIconTint(colorResource);
        item.setOnClickListener(v -> mMoveListener.selectAmbito(ambito));
        item.setIconVisibility(false);
    }

    @Override
    public int getItemCount() {
        return mAmbitosData.size();
    }

}
