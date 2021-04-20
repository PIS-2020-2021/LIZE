package com.example.lize.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;

public class AmbitosAdapter extends RecyclerView.Adapter<AmbitosAdapter.ViewHolder> {

    private String mNavTitles[]; // String Array to store the passed titles Value from MainActivity.java


    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;


        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.rowText); // Creating TextView object with the id of textView from item_row.xml
        }

        public TextView getTextView() {
            return textView;
        }
    }



    public AmbitosAdapter(String[] ambitos, String name, String email, int img_profile) {
        // MyAdapter Constructor with titles and icons parameter
        // titles, icons, name, email, profile pic are passed from the main activity as we
        this.mNavTitles = ambitos;                //have seen earlier
    }



    //Below first we override the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public AmbitosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row,parent,false); //Inflating the layout
        ViewHolder vhItem = new ViewHolder(v,viewType); //Creating ViewHolder and passing the object of type view
        return vhItem; // Returning the created object

    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(AmbitosAdapter.ViewHolder holder, int position) {

        holder.textView.setText(mNavTitles[position]); // Setting the Text with the array of our Titles

    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return mNavTitles.length; // the number of items in the list will be +1 the titles including the header view.
    }


}
