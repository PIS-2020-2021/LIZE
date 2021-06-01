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

        /**
         * Metodo para conseguir el TextView del tiempo del audio
         * @return TextView del audio
         */
        public TextView getTextView() {
            return textView;
        }

        /**
         * Metodo para conseguir el View de la imagen
         * @return View de la imagen
         */
        public ImageView getImageView(){
            return imageView;
        }

        /**
         * Metodo para crear el submenú para eliminar un documento
         * @param menu Menu a crear
         * @param v View
         * @param menuInfo Info del menu
         */
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), v.getId(), 0, "Eliminar documento");
        }

    }

    /**
     * Constructor de la clase
     * @param onDocumentListener Listener de los documentos de la nota
     */
    public DocumentAdapter(OnDocumentListener onDocumentListener){
        localDataSet = new ArrayList<>();
        this.mOnNoteListener = onDocumentListener;
    }


    /**
     * Metodo para crear nuevos views, invocado por el manager del Layout
     * @param viewGroup Grupo de Views
     * @param viewType Tipo de View
     * @return ViewHolder resultante
     */
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_attach, viewGroup, false);
        return new ViewHolder(view,mOnNoteListener);
    }

    /**
     * Metodo para cambiar el contenido de una View, invocado por el manager del Layout
     * @param viewHolder ViewHolder a cambiar
     * @param position Posicion del documento
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextView().setText(localDataSet.get(position).getName());
    }

    /**
     * Metod para conseguir el tamaño del Dataset
     * @return Tamaño del dataset
     */
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    /**
     * Metodo para añadir un documento al Dataset
     * @param document Documento a añadir
     */
    public void addDocument(Document document){
        localDataSet.add(document);
        notifyDataSetChanged();
    }

    /**
     * Metodo para eliminar un documento del Dataset
     * @param position Posición del documento
     */
    public void removeDocument(int position) {
        localDataSet.remove(position);
        notifyDataSetChanged();
    }

    /**
     * Metodo para conseguir un documento del Dataset
     * @param position Posición del documento
     */
    public Document getDocument(int position){
        return localDataSet.get(position);
    }

    /**
     * Interfaz del Listener de Documentos
     */
    public interface OnDocumentListener {
        void onDocumentClick(int position) throws FileNotFoundException;
    }
}
