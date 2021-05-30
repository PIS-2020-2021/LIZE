package com.example.lize.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.utils.Preferences;

public class MoveItem extends RecyclerView.ViewHolder{
    private TextView mTitle;
    private View mDivider;
    private ImageView mIcon;

    private String mID;

    public MoveItem(@NonNull View itemView){
        super(itemView);
        mTitle = itemView.findViewById(R.id.item_text);
        mDivider = itemView.findViewById(R.id.item_divider);
        mIcon = itemView.findViewById(R.id.item_icon);
    }

    public void bindTo(String name, String id) {
        mTitle.setText(name);
        mID = id;
    }

    public String getName(){ return mTitle.getText().toString(); }

    public String getID(){ return mID; }

    public void setOnClickListener(View.OnClickListener listener){ itemView.setOnClickListener(listener); }

    public void setDividerColor(int colorResource){ mDivider.setBackgroundColor(colorResource); }

    public void setIconTint(int colorResource){ mIcon.setColorFilter(colorResource); }

    public void setIconVisibility(boolean visible){
        mIcon.setVisibility((visible) ? View.VISIBLE : View.GONE );
    }
}
