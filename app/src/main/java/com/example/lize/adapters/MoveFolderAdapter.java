package com.example.lize.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.data.Folder;
import com.example.lize.utils.Preferences;

import java.util.ArrayList;

public class MoveFolderAdapter extends RecyclerView.Adapter<MoveItem> {
    private final Context mContext;
    private final ArrayList<Folder> mFoldersData;
    private final MoveListener mMoveListener;

    private final int moveColor;

    public interface MoveListener{
        void selectFolder(Folder folder);
    }

    public MoveFolderAdapter(Context context, ArrayList<Folder> folders, int moveColor, MoveFolderAdapter.MoveListener listener) {
        this.mContext = context;
        this.mFoldersData = folders;
        this.moveColor = moveColor;
        this.mMoveListener = listener;
    }

    @NonNull
    @Override
    public MoveItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MoveItem(LayoutInflater.from(mContext).inflate(R.layout.item_move, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MoveItem item, int position) {
        Folder folder = mFoldersData.get(position);
        int colorResource = mContext.getResources().getColor(Preferences.getAmbitoPressedColor(moveColor));
        int defaultColorResource = mContext.getResources().getColor(Preferences.getDefaultAmbitoColor());
        item.bindTo(folder.getName(), folder.getName());
        item.setDividerColor(defaultColorResource);
        item.setIconTint(colorResource);
        item.setOnClickListener(v -> mMoveListener.selectFolder(folder));
        item.setIconVisibility(true);
    }


    @Override
    public int getItemCount() {
        return mFoldersData.size();
    }

}
