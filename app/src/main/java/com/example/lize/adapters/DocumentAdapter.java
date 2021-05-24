package com.example.lize.adapters;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lize.R;
import com.example.lize.data.Document;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {
    private final ArrayList<Document> localDataSet;
    private final OnDocumentListener mOnNoteListener;
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnClickListener {
        private final TextView textView;
        private final ImageView imageView;
        OnDocumentListener onDocumentListener;

        public ViewHolder(View view,OnDocumentListener onDocumentListener) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.DocumentTitle);
            imageView = (ImageView) view.findViewById(R.id.DocImage);
            imageView.setOnCreateContextMenuListener(this);
            this.onDocumentListener = onDocumentListener;
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            try {
                onDocumentListener.onDocumentClick(getAdapterPosition());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageView(){
            return imageView;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), v.getId(), 0, "Eliminar documento");
        }

    }

    public DocumentAdapter(OnDocumentListener onDocumentListener){
        localDataSet = new ArrayList<>();
        this.mOnNoteListener = onDocumentListener;
    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_attach, viewGroup, false);

        return new ViewHolder(view,mOnNoteListener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextView().setText(localDataSet.get(position).getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void addDocument(Document document){
        localDataSet.add(document);
        notifyDataSetChanged();
    }
    public void removeDocument(int position) {
        localDataSet.remove(position);
        notifyDataSetChanged();
    }

    public Document getDocument(int position){
        return localDataSet.get(position);
    }



    public interface OnDocumentListener {
        void onDocumentClick(int position) throws FileNotFoundException;
    }
}
