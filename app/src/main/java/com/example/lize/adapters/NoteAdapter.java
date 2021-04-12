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
 * de Notas, con el correspondiente CardNote (ViewHolder). Podemos distinguir dos tipos de ViewHolders;
 * <ul>
 *      <li>cardNotes con alto height {@link R.dimen#cardnote_layout_height_high} (cardNoteType = true)</li>
 *      <li>cardNotes con bajo height {@link R.dimen#cardnote_layout_height_high} (cardNoteType = false) </li>
 * </ul>
 * Puesto que solo el height es modificado, nos basta con un único tipo de ViewHolder {@link CardNote}.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.CardNote>{

    private ArrayList<Note> mNotesData;
    private Context mContext;
    private boolean cardNoteType;

    /**
     * Constructor que pasa el listado de notas i el contexto.
     * @param context contexto de la app
     * @param notesData ArrayList conteniendo la información de las notas.
     */
    public NoteAdapter(Context context, ArrayList<Note> notesData) {
        this.mNotesData = notesData;
        this.mContext = context;
        this.cardNoteType = true;
    }

    /**
     * Modificamos el cardNoteType, y actualizamos el RecicleView llamando a {@link #notifyDataSetChanged()}
     */
    public void changeCardNoteType(){
        this.cardNoteType = !cardNoteType;
        super.notifyDataSetChanged();
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
     * Modificamos la altura del Holder según el valor de cardNoteType.
     * @param holder ViewHolder a quien pasar los datos.
     * @param position Position del adaptador.
     */
    @Override
    public void onBindViewHolder(@NonNull CardNote holder, int position) {
        // Obtenemos los parámetros del Layout correspondiente al holder y modificamos el height
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.height = holder.itemView.getResources().getDimensionPixelSize((cardNoteType) ?
                R.dimen.cardnote_layout_height_high : R.dimen.cardnote_layout_height_low);

        holder.itemView.setLayoutParams(params);        // Seteamos el nuevo Layout
        Note currentNote = mNotesData.get(position);    // Obtenemos la nota de la posición
        holder.bindTo(currentNote);                     // Enlazamos la nota con el ViewHolder
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