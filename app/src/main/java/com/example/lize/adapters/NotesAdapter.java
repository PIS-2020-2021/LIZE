package com.example.lize.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.data.Note;

import java.util.ArrayList;

/**
 * Adaptador del RecyclerView de Notas, en la actividad principal. Enlaza los datos del dataSet
 * de Notas, con el correspondiente Card,
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.CardNote>{

    private ArrayList<Note> mNotesData;
    private Context mContext;

    /**
     * Constructor que pasa el listado de notas i el contexto.
     * @param context contexto de la app
     * @param notesData ArrayList conteniendo la información de las notas.
     */
    public NotesAdapter(Context context, ArrayList<Note> notesData) {
        this.mNotesData = notesData;
        this.mContext = context;
    }

    /**
     * Generador de ViewHolders de notas.
     * @param parent ViewGroup correspondiente al RecycleView - contenedor de notas.
     * @param viewType Tipo de vista del ViewHolder
     * @return El nuevo ViewHolder
     */
    @NonNull
    @Override
    public CardNote onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardNote(LayoutInflater.from(mContext).inflate(R.layout.note_card, parent, false));
    }

    /**
     * Método requerido que permite enlazar los datos de la nota con el correspondiente ViewHolder.
     * @param holder ViewHolder a quien pasar los datos.
     * @param position Position del adaptador.
     */
    @Override
    public void onBindViewHolder(@NonNull CardNote holder, int position) {
        Note currentNote = mNotesData.get(position); // Obtenemos la nota de la posición
        holder.bindTo(currentNote); // Enlazamos la nota con el ViewHolder
    }

    /**
     * Método que requiere el adaptador para determinar el tamaño del dataSet
     * @return tamaño DataSet
     */
    @Override
    public int getItemCount() {
        return mNotesData.size();
    }

    /**
     * ViewHolder class que se corresponde con los Cards de las notas
     */
    public class CardNote extends RecyclerView.ViewHolder{

        private TextView mTitleNote;
        private TextView mTextNote;

        /**
         * Constructor del ViewHolder correspondiente al layout de note_card
         * @param itemView rootview del fichero note_card.xml
         */
        public CardNote(@NonNull View itemView) {
            super(itemView);
            // Inicializamos los componentes del layout
            mTitleNote = (TextView) itemView.findViewById(R.id.note_name);
            mTextNote = (TextView) itemView.findViewById(R.id.note_body);
        }

        /**
         * Método para <b>enlazar</b> los datos de la nota con el Card de este objeto ViewHolder.
         * @param currentNote nota actual
         */
        public void bindTo(Note currentNote) {
            mTitleNote.setText(currentNote.getTitle());
            mTextNote.setText(currentNote.getText());
        }
    }
}
