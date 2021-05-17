package com.example.lize.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.data.Note;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    private final Context mContext;
    private final ArrayList<Note> mNotesData;
    private NoteAdapter.CardNoteListener customListener;
    private boolean cardNoteType;

    /* Custom CardNote onClick Listener */
    public interface CardNoteListener{
        void onCardNoteClicked(NoteAdapter.CardNote cardNote);
        void onCardNoteSelected(NoteAdapter.CardNote cardNote);
    }

    /**
     * Method for registering a CardNote onClick listener
     * @param listener Observer which knows when the chip is clicked.
     */
    public void registerCardNoteListener(NoteAdapter.CardNoteListener listener){
        customListener = listener;
    }

    /**
     * Constructor que pasa el listado de notas i el contexto.
     * @param context contexto de la app
     * @param notesData ArrayList conteniendo la información de las notas.
     */
    public NoteAdapter(Context context, ArrayList<Note> notesData, boolean cardNoteType) {
        this.mNotesData = notesData;
        this.mContext = context;
        this.cardNoteType = cardNoteType;
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
        holder.mTitleNote.setMaxHeight(mContext.getResources().getDimensionPixelSize((cardNoteType) ?
                R.dimen.cardnote_layout_height_high : R.dimen.cardnote_layout_height_low));

        /*ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.height = holder.itemView.getResources().getDimensionPixelSize((cardNoteType) ?
                R.dimen.cardnote_layout_height_high : R.dimen.cardnote_layout_height_low);

        holder.itemView.setLayoutParams(params);        // Seteamos el nuevo Layout*/


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

        public final MaterialCardView holder;
        public final TextView mMetadataText;
        public final TextView mTitleNote;
        public final TextView mTextNote;

        public final MaterialButton mMoveBtn, mCopyBtn, mDeleteBtn;

        public int BASE_HEIGHT;

        public String mNoteID;

        /**
         * Constructor del ViewHolder correspondiente al layout de note_card
         * @param itemView rootview del fichero note_card.xml
         */
        public CardNote(@NonNull View itemView) {
            super(itemView);
            // Inicializamos los componentes del layout
            holder = (MaterialCardView) itemView;
            mMetadataText = (TextView) itemView.findViewById(R.id.note_metadata);
            mTitleNote = (TextView) itemView.findViewById(R.id.note_title);
            mTextNote = (TextView) itemView.findViewById(R.id.note_body);
            mMoveBtn = (MaterialButton) itemView.findViewById(R.id.note_move_btn);
            mCopyBtn = (MaterialButton) itemView.findViewById(R.id.note_copy_btn);
            mDeleteBtn = (MaterialButton) itemView.findViewById(R.id.note_delete_btn);
            BASE_HEIGHT = holder.getHeight();

            if (customListener != null){
                holder.setOnClickListener((v) -> performClick());
                holder.setOnLongClickListener((v) -> performLongClick());
            }
        }

        public float getHeight(){
            return (float) holder.getLayoutParams().height;
        }

        public void setHeight(float newHeight){
            ViewGroup.LayoutParams params = holder.getLayoutParams();
            params.height = (int) newHeight;
            holder.setLayoutParams(params);
        }

        public float getTranslationY(){
            return mMoveBtn.getTranslationY();
        }

        public void setTranslationY(float translationY){
            //setHeight(getHeight() + translationY);
            mMoveBtn.setTranslationY(translationY);
            mCopyBtn.setTranslationY(translationY);
            mDeleteBtn.setTranslationY(translationY);
        }

        public float getAlpha(){
            return mMoveBtn.getAlpha();
        }

        public void setAlpha(float alpha){
            mMoveBtn.setAlpha(alpha);
            mCopyBtn.setAlpha(alpha);
            mDeleteBtn.setAlpha(alpha);
        }

        public String getNoteID(){ return mNoteID; }

        public void performClick() {
            customListener.onCardNoteClicked(this);
        }

        public boolean performLongClick() {
            holder.setChecked(!holder.isChecked());
            mMoveBtn.setAlpha(0.0f);
            mCopyBtn.setAlpha(0.0f);
            mDeleteBtn.setAlpha(0.0f);
            mMoveBtn.setVisibility(View.VISIBLE);
            mCopyBtn.setVisibility(View.VISIBLE);
            mDeleteBtn.setVisibility(View.VISIBLE);
            customListener.onCardNoteSelected(this);
            return true;
        }

        /**
         * Método para <b>enlazar</b> los datos de la nota con el Card de este objeto ViewHolder.
         * @param currentNote nota actual
         */
        public void bindTo(Note currentNote) {
            mTitleNote.setText(currentNote.getTitle());
            mTextNote.setText(currentNote.getText_plain());
            DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");

            String metadata = "";
            if (currentNote.getFolderTAG() != null) metadata += currentNote.getFolderTAG();
            if (0 < metadata.length() && currentNote.getLastUpdate() != null)
                metadata += " - " + dateFormat.format(currentNote.getLastUpdate());
            else if(currentNote.getLastUpdate() != null)
                metadata += dateFormat.format(currentNote.getLastUpdate());

            mMetadataText.setText(metadata);
            mNoteID = currentNote.getSelfID();
            BASE_HEIGHT = holder.getHeight();
        }

        public boolean isSelected() { return holder.isChecked(); }

        public void select(){ holder.setChecked(!holder.isChecked()); }

        // Resets this Carnote to default state
        public void reset() {
            holder.setChecked(false);
            mMoveBtn.setVisibility(View.INVISIBLE);
            mCopyBtn.setVisibility(View.INVISIBLE);
            mDeleteBtn.setVisibility(View.INVISIBLE);
            setAlpha(0.0f);
            setTranslationY(0.0f);
            setHeight(BASE_HEIGHT);
        }

    }
}
