package com.example.lize.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.data.Note;
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
        void onCardNoteMoved(NoteAdapter.CardNote cardNote);
        void onCardNoteCopy(NoteAdapter.CardNote cardNote);
        void onCardNoteDelete(NoteAdapter.CardNote cardNote);
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

        private final MaterialCardView holder;
        private final TextView mMetadataText;
        private final TextView mTitleNote;
        private final TextView mTextNote;
        private final View mButtonGroup;

        private String mNoteID;

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

            ImageButton mMoveBtn = itemView.findViewById(R.id.note_move_btn);
            ImageButton mCopyBtn = itemView.findViewById(R.id.note_copy_btn);
            ImageButton mDeleteBtn = itemView.findViewById(R.id.note_delete_btn);

            mButtonGroup = itemView.findViewById(R.id.button_group);

            if (customListener != null){
                holder.setOnClickListener((v) -> performClick());
                holder.setOnLongClickListener((v) -> performLongClick());
                mMoveBtn.setOnClickListener((v) -> customListener.onCardNoteMoved(this));
                mCopyBtn.setOnClickListener((v) -> customListener.onCardNoteCopy(this));
                mDeleteBtn.setOnClickListener((v) -> customListener.onCardNoteDelete(this));
            }
        }

        public String getNoteID(){ return mNoteID; }

        /**
         * Método para <b>enlazar</b> los datos de la nota con el Card de este objeto ViewHolder.
         * @param currentNote nota actual
         */
        public void bindTo(Note currentNote) {
            mTitleNote.setText(currentNote.getTitle());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mTextNote.setText(Html.fromHtml(currentNote.getText_html(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                mTextNote.setText(Html.fromHtml(currentNote.getText_html()));
            }

            DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");

            String metadata = "";
            if (currentNote.getFolderTAG() != null) metadata += currentNote.getFolderTAG();
            if (0 < metadata.length() && currentNote.getLastUpdate() != null)
                metadata += " - " + dateFormat.format(currentNote.getLastUpdate());
            else if(currentNote.getLastUpdate() != null)
                metadata += dateFormat.format(currentNote.getLastUpdate());

            mMetadataText.setText(metadata);
            mNoteID = currentNote.getSelfID();
        }

        public void performClick() {
            customListener.onCardNoteClicked(this);
        }

        public boolean performLongClick() {
            holder.setChecked(!holder.isChecked());
            if ((holder.isChecked())){
                holder.setCardElevation(mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_selected_elevation));
                holder.setStrokeWidth(mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_selected_stroke_width));
                showButtonGroup();
            } else{
                hideButtonGroup();
                holder.setCardElevation(mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_default_elevation));
                holder.setStrokeWidth(mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_default_stroke_width));
            }
            customListener.onCardNoteSelected(this);
            return true;
        }

        public boolean isSelected() { return holder.isChecked(); }

        public void select(){ holder.setChecked(!holder.isChecked()); }

        public void reset() {
            hideButtonGroup();
            holder.setCardElevation(mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_default_elevation));
            holder.setStrokeWidth(mContext.getResources().getDimensionPixelSize(R.dimen.cardnote_default_stroke_width));
            holder.setChecked(false);
        }

        /* CardNote animation for showing the button Group */
        private void showButtonGroup(){
            int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) mButtonGroup.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
            int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            mButtonGroup.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
            final int targetHeight = mButtonGroup.getMeasuredHeight();

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            mButtonGroup.getLayoutParams().height = 1;
            mButtonGroup.setVisibility(View.VISIBLE);

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    mButtonGroup.getLayoutParams().height = interpolatedTime == 1
                            ? ViewGroup.LayoutParams.WRAP_CONTENT : (int)(targetHeight * interpolatedTime);
                    mButtonGroup.setAlpha(interpolatedTime);
                    mButtonGroup.requestLayout();
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            // Expansion speed of 1dp/ms
            a.setDuration(4 * (int)(targetHeight / mButtonGroup.getContext().getResources().getDisplayMetrics().density));
            mButtonGroup.startAnimation(a);
        }

        /* CardNote animation for hidding the button Group */
        private void hideButtonGroup(){
            final int initialHeight = mButtonGroup.getMeasuredHeight();

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if(interpolatedTime == 1){
                        mButtonGroup.setAlpha(0.0f);
                        mButtonGroup.setVisibility(View.GONE);

                    }else{
                        mButtonGroup.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                        mButtonGroup.setAlpha(1.0f - interpolatedTime);
                        mButtonGroup.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            // Collapse speed of 1dp/ms
            a.setDuration(4 * (int)(initialHeight / mButtonGroup.getContext().getResources().getDisplayMetrics().density));
            mButtonGroup.startAnimation(a);
        }

    }
}
